"""Exercise the real installer against an isolated fake GitHub endpoint."""
import hashlib
import json
import os
from pathlib import Path
import subprocess
import tempfile
import zipfile

installer = Path('scripts/install-latest.sh').resolve()
with tempfile.TemporaryDirectory() as directory:
    root = Path(directory)
    binary = root / 'bin'
    binary.mkdir()
    jar = root / 'source.jar'
    with zipfile.ZipFile(jar, 'w') as z:
        z.writestr('META-INF/mods.toml', 'modId="siege"')
    manifest = {'jar': 'siege-menu-0.11.0.jar', 'commit': 'a' * 40,
                'sha256': hashlib.sha256(jar.read_bytes()).hexdigest()}
    (root / 'manifest.json').write_text(json.dumps(manifest))
    gh = binary / 'gh'
    gh.write_text('''#!/usr/bin/env python3
import os, sys
from pathlib import Path
r = Path(os.environ['SIEGE_TEST_ROOT'])
a = ' '.join(sys.argv[1:])
if a.startswith('auth'): sys.exit(0)
if '/commits/' in a: print('b' * 40)
elif 'manifest.json' in a: sys.stdout.buffer.write((r/'manifest.json').read_bytes())
else: sys.stdout.buffer.write((r/'source.jar').read_bytes())
''')
    gh.chmod(0o755)
    env = dict(os.environ, PATH=str(binary) + os.pathsep + os.environ['PATH'], SIEGE_TEST_ROOT=str(root))
    mods = root / 'instance' / 'mods'
    mods.mkdir(parents=True)
    old = mods / 'siege-menu-0.10.5.jar'
    old.write_bytes(b'previous installation')
    jar.write_bytes(jar.read_bytes() + b'changed')
    failed = subprocess.run(['bash', str(installer), str(mods)], env=env, capture_output=True)
    assert failed.returncode != 0 and old.read_bytes() == b'previous installation', 'Bad hash replaced old mod'
    manifest['sha256'] = hashlib.sha256(jar.read_bytes()).hexdigest()
    (root / 'manifest.json').write_text(json.dumps(manifest))
    success = subprocess.run(['bash', str(installer), str(mods)], env=env, capture_output=True)
    assert success.returncode == 0, success.stderr.decode()
    assert (mods / manifest['jar']).read_bytes() == jar.read_bytes()
    assert not old.exists()
    backups = list((root / 'instance').glob('siege-backup-*/siege-menu-0.10.5.jar'))
    assert len(backups) == 1 and backups[0].read_bytes() == b'previous installation'
    assert not list(mods.glob('.siege-stage-*'))
print('Installer checksum rejection, replacement and backup passed')
