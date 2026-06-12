#!/usr/bin/env node
import chalk from "chalk";
import { runCli } from "../src/generator.js";

const args = process.argv.slice(2);

async function main(): Promise<void> {
  await runCli(args);
}

main().catch((error: unknown) => {
  const message = error instanceof Error ? error.message : String(error);
  console.error(chalk.red(`Fatal error: ${message}`));
  process.exit(1);
});
