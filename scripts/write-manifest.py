import hashlib
import json
import pathlib
import sys
import zipfile

jar = pathlib.Path(sys.argv[1])
with zipfile.ZipFile(jar) as archive:
    assert archive.testzip() is None, 'Invalid jar'
    assert 'META-INF/mods.toml' in archive.namelist(), 'Missing Forge metadata'
manifest = {'jar': jar.name, 'version': jar.name.removeprefix('siege-menu-').removesuffix('.jar'),
            'commit': sys.argv[2], 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest()}
pathlib.Path('dist/manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
