import { cpSync, mkdirSync, rmSync } from 'fs';
import { join } from 'path';

const source = join('dist', 'frontend', 'browser');
const target = 'browser';

rmSync(target, { recursive: true, force: true });
mkdirSync(target, { recursive: true });
cpSync(source, target, { recursive: true });

console.log(`Vercel output ready: ${target}/`);
