import type { TemplateName } from "./prompts.js";

const TEMPLATE_NAMES: TemplateName[] = ["service-blank", "service-crud"];

export type ParsedCliArgs = {
  positional: string[];
  nonInteractive: boolean;
  template?: TemplateName;
  initializeGit?: boolean;
  companyName?: string;
  basePackage?: string;
  starterVersion?: string;
};

export function parseArgv(argv: string[]): ParsedCliArgs {
  const positional: string[] = [];
  let nonInteractive = false;
  let template: TemplateName | undefined;
  let initializeGit: boolean | undefined;
  let companyName: string | undefined;
  let basePackage: string | undefined;
  let starterVersion: string | undefined;

  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];

    if (arg === "--yes" || arg === "-y") {
      nonInteractive = true;
      continue;
    }

    if (arg === "--template" || arg === "-t") {
      const value = argv[++i];
      if (value && TEMPLATE_NAMES.includes(value as TemplateName)) {
        template = value as TemplateName;
        nonInteractive = true;
      }
      continue;
    }

    if (arg === "--git") {
      initializeGit = true;
      continue;
    }

    if (arg === "--no-git") {
      initializeGit = false;
      continue;
    }

    if (arg === "--company" || arg === "-c") {
      companyName = argv[++i]?.trim();
      continue;
    }

    if (arg === "--package" || arg === "-p") {
      basePackage = argv[++i]?.trim();
      continue;
    }

    if (arg === "--starter-version") {
      starterVersion = argv[++i]?.trim();
      continue;
    }

    if (!arg.startsWith("-")) {
      positional.push(arg);
    }
  }

  return {
    positional,
    nonInteractive,
    template,
    initializeGit,
    companyName,
    basePackage,
    starterVersion,
  };
}
