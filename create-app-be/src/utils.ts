import path from "node:path";
import fs from "fs-extra";

const APP_NAME_REGEX = /^[a-z][a-z0-9-]*[a-z0-9]$|^[a-z]$/;
const PACKAGE_REGEX = /^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*)+$/;

export function createNodeModulesFilter(rootDir: string): (src: string) => boolean {
  return (src: string) => {
    const relativePath = path.relative(rootDir, src);
    if (!relativePath || relativePath.startsWith("..")) {
      return true;
    }
    const segments = relativePath.split(path.sep).filter(Boolean);
    return !segments.includes("node_modules");
  };
}

export async function copyDirectoryContents(
  srcDir: string,
  destDir: string,
  options?: { filter?: (src: string) => boolean },
): Promise<void> {
  const filter = options?.filter;
  await fs.ensureDir(destDir);

  const entries = await fs.readdir(srcDir);
  for (const entry of entries) {
    const srcPath = path.join(srcDir, entry);
    if (filter && !filter(srcPath)) {
      continue;
    }
    const destPath = path.join(destDir, entry);
    await fs.copy(srcPath, destPath, {
      overwrite: true,
      errorOnExist: false,
      filter: filter ? (src) => filter(src) : undefined,
    });
  }
}

export function isValidAppName(name: string): boolean {
  return APP_NAME_REGEX.test(name);
}

export function isValidPackageName(name: string): boolean {
  return PACKAGE_REGEX.test(name);
}

export function slugify(input: string): string {
  return input
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "")
    .replace(/^[0-9]+/, "");
}

export function toPascalCase(kebab: string): string {
  return kebab
    .split("-")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join("");
}

export function toTitleCase(input: string): string {
  return input
    .replace(/[-_]/g, " ")
    .split(" ")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
}

export function resolveTargetDir(cwd: string, appName: string): string {
  return path.resolve(cwd, appName);
}

export type PackageIdentity = {
  appName: string;
  artifactId: string;
  groupId: string;
  basePackage: string;
  basePackagePath: string;
  applicationClass: string;
};

export function derivePackageIdentity(
  appName: string,
  company: string,
  basePackageOverride?: string,
): PackageIdentity {
  const artifactId = appName;
  const applicationClass = `${toPascalCase(appName)}Application`;

  if (basePackageOverride) {
    if (!isValidPackageName(basePackageOverride)) {
      throw new Error(`Invalid package name: ${basePackageOverride}`);
    }
    const segments = basePackageOverride.split(".");
    const groupId = segments.length >= 2 ? `${segments[0]}.${segments[1]}` : basePackageOverride;
    return {
      appName,
      artifactId,
      groupId,
      basePackage: basePackageOverride,
      basePackagePath: basePackageOverride.replace(/\./g, "/"),
      applicationClass,
    };
  }

  const companySlug = slugify(company) || "omobio";
  const serviceSlug = appName.replace(/-/g, "");
  const basePackage = `com.${companySlug}.${serviceSlug}`;
  return {
    appName,
    artifactId,
    groupId: `com.${companySlug}`,
    basePackage,
    basePackagePath: basePackage.replace(/\./g, "/"),
    applicationClass,
  };
}
