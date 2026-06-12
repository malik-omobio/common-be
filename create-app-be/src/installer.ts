import { execa } from "execa";

export async function initializeGit(targetDir: string): Promise<void> {
  await execa("git", ["init"], { cwd: targetDir, stdio: "inherit" });
}
