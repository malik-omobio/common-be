import fs from "fs-extra";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const packageRoot = path.resolve(__dirname, "..");
const platformRoot = path.resolve(packageRoot, "..");

const from = path.join(platformRoot, "templates");
const to = path.join(packageRoot, "templates");

function shouldCopy(src) {
  if (src.includes(`${path.sep}node_modules${path.sep}`)) {
    return false;
  }
  if (src.includes(`${path.sep}.git${path.sep}`)) {
    return false;
  }
  if (src.includes(`${path.sep}target${path.sep}`)) {
    return false;
  }
  return true;
}

if (!(await fs.pathExists(from))) {
  throw new Error(`Missing publish asset: ${from}`);
}

await fs.remove(to);
await fs.copy(from, to, { filter: (src) => shouldCopy(src) });
console.log(`Copied ${path.relative(platformRoot, from)} -> ${path.relative(packageRoot, to)}`);
console.log("Publish assets ready.");
