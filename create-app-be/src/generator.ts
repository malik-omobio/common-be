import path from "node:path";
import chalk from "chalk";
import fs from "fs-extra";
import ora from "ora";
import { parseArgv } from "./cli-args.js";
import { askOverwriteConfirmation, askQuestions } from "./prompts.js";
import { initializeGit } from "./installer.js";
import { replacePlaceholders } from "./replace-placeholders.js";
import { renameTemplatePaths } from "./rename-paths.js";
import { resolveCreateAppBePackageRoot, resolveTemplateDir } from "./paths.js";
import {
  copyDirectoryContents,
  createNodeModulesFilter,
  derivePackageIdentity,
  isValidAppName,
  resolveTargetDir,
  toTitleCase,
} from "./utils.js";

export async function runCli(args: string[]): Promise<void> {
  const parsed = parseArgv(args);
  const appNameArg = parsed.positional[0]?.trim();
  if (appNameArg && !isValidAppName(appNameArg)) {
    throw new Error("Invalid app name. Use lowercase letters, numbers, and hyphens.");
  }

  const answers = await askQuestions(appNameArg, parsed);
  const identity = derivePackageIdentity(answers.appName, answers.companyName, answers.basePackage);
  const cwd = process.cwd();
  const targetDir = resolveTargetDir(cwd, answers.appName);

  if (await fs.pathExists(targetDir)) {
    const shouldOverwrite =
      parsed.nonInteractive || (await askOverwriteConfirmation(answers.appName));
    if (!shouldOverwrite) {
      throw new Error("Aborted to avoid overwriting existing directory.");
    }
    await fs.emptyDir(targetDir);
  } else {
    await fs.ensureDir(targetDir);
  }

  const templateDir = resolveTemplateDir(answers.template);
  if (!(await fs.pathExists(templateDir))) {
    throw new Error(`Template not found: ${templateDir}`);
  }

  const skipNodeModules = createNodeModulesFilter(templateDir);
  const templateSpinner = ora(`Applying "${answers.template}" template...`).start();
  await copyDirectoryContents(templateDir, targetDir, { filter: skipNodeModules });
  templateSpinner.succeed("Template copied.");

  const title = toTitleCase(answers.appName);
  const replacements = {
    "__APP_NAME__": answers.appName,
    "__APP_TITLE__": title,
    "__ARTIFACT_ID__": identity.artifactId,
    "__GROUP_ID__": identity.groupId,
    "__BASE_PACKAGE__": identity.basePackage,
    "__BASE_PACKAGE_PATH__": identity.basePackagePath,
    "__APPLICATION_CLASS__": identity.applicationClass,
    "__STARTER_VERSION__": answers.starterVersion,
  };

  const replaceSpinner = ora("Applying placeholders...").start();
  await replacePlaceholders(targetDir, replacements);
  await renameTemplatePaths(targetDir, identity.basePackagePath, identity.applicationClass);
  replaceSpinner.succeed("Placeholders applied.");

  const pomPath = path.join(targetDir, "pom.xml");
  if (!(await fs.pathExists(pomPath))) {
    throw new Error(`Scaffold incomplete: missing pom.xml in ${targetDir}`);
  }

  const metadataPath = path.join(targetDir, ".omobio.json");
  try {
    const cliPkg = await fs.readJson(path.join(resolveCreateAppBePackageRoot(), "package.json"));
    await fs.writeJson(
      metadataPath,
      {
        createAppBe: {
          template: answers.template,
          starterVersion: answers.starterVersion,
          basePackage: identity.basePackage,
          version: cliPkg.version ?? "0.0.0",
          createdAt: new Date().toISOString(),
        },
      },
      { spaces: 2 },
    );
  } catch {
    // metadata is optional
  }

  if (answers.initializeGit) {
    const gitSpinner = ora("Initializing git repository...").start();
    await initializeGit(targetDir);
    gitSpinner.succeed("Git repository initialized.");
  }

  console.log(chalk.green("\nSuccess! Your Spring Boot service is ready.\n"));
  console.log(`  Package:  ${identity.basePackage}`);
  console.log(`  Main:     ${identity.applicationClass}.java\n`);
  console.log("Next steps:");
  console.log(`  cd ${answers.appName}`);
  console.log("  docker compose up -d");
  console.log(
    `  mvn spring-boot:run "-Dspring-boot.run.profiles=dev,seed"`,
  );
  console.log("\nDefault admin: admin@example.com / admin123");
  console.log("Health check:  http://localhost:8080/api/v1/health\n");
}
