import fs from "fs-extra";
import path from "node:path";

const BINARY_EXTENSIONS = new Set([".png", ".jpg", ".jpeg", ".gif", ".webp", ".ico", ".woff", ".woff2"]);

export async function replacePlaceholders(
  rootDir: string,
  replacements: Record<string, string>,
): Promise<void> {
  const entries = await fs.readdir(rootDir);

  for (const entry of entries) {
    const absolute = path.join(rootDir, entry);
    const stats = await fs.stat(absolute);

    if (stats.isDirectory()) {
      await replacePlaceholders(absolute, replacements);
      continue;
    }

    if (BINARY_EXTENSIONS.has(path.extname(absolute))) {
      continue;
    }

    const content = await fs.readFile(absolute, "utf8");
    const next = Object.entries(replacements).reduce(
      (acc, [key, value]) => acc.replaceAll(key, value),
      content,
    );

    if (next !== content) {
      await fs.writeFile(absolute, next, "utf8");
    }
  }
}
