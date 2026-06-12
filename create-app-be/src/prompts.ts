import prompts from "prompts";
import type { ParsedCliArgs } from "./cli-args.js";
import { isValidAppName } from "./utils.js";

export type TemplateName = "service-blank" | "service-crud";

const DEFAULT_COMPANY_NAME = "Omobio";
const DEFAULT_STARTER_VERSION = "1.0.0";

export type CliAnswers = {
  appName: string;
  template: TemplateName;
  initializeGit: boolean;
  companyName: string;
  basePackage?: string;
  starterVersion: string;
};

export async function askQuestions(
  initialAppName?: string,
  parsed?: ParsedCliArgs,
): Promise<CliAnswers> {
  if (parsed?.nonInteractive) {
    const appName = (initialAppName ?? parsed.positional[0] ?? "").trim();
    if (!isValidAppName(appName)) {
      throw new Error(
        "Non-interactive mode requires a valid app name: create-app-be <name> --template <service-blank|service-crud> -y",
      );
    }
    if (!parsed.template) {
      throw new Error("Non-interactive mode requires --template service-blank|service-crud");
    }

    return {
      appName,
      template: parsed.template,
      initializeGit: parsed.initializeGit ?? false,
      companyName: parsed.companyName?.trim() || DEFAULT_COMPANY_NAME,
      basePackage: parsed.basePackage,
      starterVersion: parsed.starterVersion ?? DEFAULT_STARTER_VERSION,
    };
  }

  const responses = await prompts(
    [
      {
        type: initialAppName ? null : "text",
        name: "appName",
        message: "Service name (Maven artifact, lowercase, e.g. my-hr-service)",
        validate: (value: string) => {
          const trimmed = value.trim();
          if (!trimmed) {
            return "Name is required.";
          }
          if (isValidAppName(trimmed)) {
            return true;
          }
          return "Use lowercase letters, numbers, and hyphens (e.g. my-hr-service).";
        },
      },
      {
        type: "select",
        name: "template",
        message: "Select a template",
        choices: [
          { title: "service-blank — auth + health only", value: "service-blank" },
          { title: "service-crud — Employee CRUD example", value: "service-crud" },
        ],
      },
      {
        type: "text",
        name: "companyName",
        message: "Company name (used for Java package, e.g. Acme)",
        initial: DEFAULT_COMPANY_NAME,
      },
      {
        type: "confirm",
        name: "initializeGit",
        message: "Initialize git repository?",
        initial: false,
      },
    ],
    {
      onCancel: () => {
        throw new Error("Operation cancelled by user.");
      },
    },
  );

  const appName = (initialAppName ?? responses.appName ?? "").trim();
  if (!isValidAppName(appName)) {
    throw new Error("Invalid service name.");
  }

  return {
    appName,
    template: responses.template,
    initializeGit: parsed?.initializeGit ?? Boolean(responses.initializeGit),
    companyName: responses.companyName?.trim() || DEFAULT_COMPANY_NAME,
    basePackage: parsed?.basePackage,
    starterVersion: parsed?.starterVersion ?? DEFAULT_STARTER_VERSION,
  };
}

export async function askOverwriteConfirmation(appName: string): Promise<boolean> {
  const response = await prompts({
    type: "confirm",
    name: "overwrite",
    message: `Directory "${appName}" already exists. Overwrite?`,
    initial: false,
  });

  return Boolean(response.overwrite);
}
