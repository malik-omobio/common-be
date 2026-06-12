import fs from "fs-extra";
import path from "node:path";

const PLACEHOLDER_DIR = "__BASE_PACKAGE_PATH__";
const PLACEHOLDER_APP_FILE = "__APPLICATION_CLASS__";

export async function renameTemplatePaths(
  rootDir: string,
  basePackagePath: string,
  applicationClass: string,
): Promise<void> {
  await renamePlaceholderDirectories(rootDir, basePackagePath);
  await renamePlaceholderFiles(rootDir, applicationClass);
}

async function renamePlaceholderDirectories(rootDir: string, basePackagePath: string): Promise<void> {
  const entries = await fs.readdir(rootDir, { withFileTypes: true });

  for (const entry of entries) {
    const absolute = path.join(rootDir, entry.name);

    if (entry.isDirectory()) {
      if (entry.name === PLACEHOLDER_DIR) {
        const parent = path.dirname(absolute);
        const targetRoot = path.join(parent, ...basePackagePath.split("/"));
        await fs.ensureDir(path.dirname(targetRoot));
        await fs.move(absolute, targetRoot, { overwrite: true });
        continue;
      }
      await renamePlaceholderDirectories(absolute, basePackagePath);
    }
  }
}

async function renamePlaceholderFiles(rootDir: string, applicationClass: string): Promise<void> {
  const entries = await fs.readdir(rootDir, { withFileTypes: true });

  for (const entry of entries) {
    const absolute = path.join(rootDir, entry.name);

    if (entry.isDirectory()) {
      await renamePlaceholderFiles(absolute, applicationClass);
      continue;
    }

    if (entry.name.startsWith(`${PLACEHOLDER_APP_FILE}.`)) {
      const extension = path.extname(entry.name);
      const target = path.join(path.dirname(absolute), `${applicationClass}${extension}`);
      await fs.move(absolute, target, { overwrite: true });
    }
  }
}
