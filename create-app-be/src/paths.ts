import path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "fs-extra";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export function resolveCreateAppBePackageRoot(): string {
  const root = findCreateAppBePackageRoot();
  if (!root) {
    throw new Error("Could not locate create-app-be package root.");
  }
  return root;
}

function findCreateAppBePackageRoot(): string | null {
  let dir = __dirname;
  for (let depth = 0; depth < 10; depth += 1) {
    const packageJsonPath = path.join(dir, "package.json");
    if (fs.existsSync(packageJsonPath)) {
      try {
        const pkg = fs.readJsonSync(packageJsonPath) as { name?: string };
        if (pkg.name === "@omobio/create-app-be" || pkg.name === "create-app-be") {
          return dir;
        }
      } catch {
        // ignore invalid package.json
      }
    }
    const parent = path.dirname(dir);
    if (parent === dir) {
      break;
    }
    dir = parent;
  }
  return null;
}

function isPlatformRoot(dir: string): boolean {
  return (
    fs.existsSync(path.join(dir, "templates", "service-crud")) &&
    fs.existsSync(path.join(dir, "spring-base-starter"))
  );
}

export function resolvePlatformRoot(): string {
  const packageRoot = findCreateAppBePackageRoot();

  if (packageRoot) {
    const monorepoRoot = path.dirname(packageRoot);
    if (isPlatformRoot(monorepoRoot)) {
      return monorepoRoot;
    }
  }

  const searchRoots = packageRoot
    ? [packageRoot, path.dirname(packageRoot), path.dirname(path.dirname(packageRoot))]
    : [__dirname];

  const seen = new Set<string>();
  for (const start of searchRoots) {
    let dir = start;
    for (let depth = 0; depth < 10; depth += 1) {
      if (seen.has(dir)) {
        break;
      }
      seen.add(dir);

      if (fs.existsSync(path.join(dir, "templates", "service-crud"))) {
        return dir;
      }

      const parent = path.dirname(dir);
      if (parent === dir) {
        break;
      }
      dir = parent;
    }
  }

  throw new Error(
    "Could not locate templates/. Reinstall create-app-be or run from the common-be monorepo.",
  );
}

export function resolveTemplateDir(templateName: string): string {
  const packageRoot = findCreateAppBePackageRoot();
  if (packageRoot && fs.existsSync(path.join(packageRoot, "templates", templateName))) {
    return path.resolve(packageRoot, "templates", templateName);
  }
  return path.resolve(resolvePlatformRoot(), "templates", templateName);
}
