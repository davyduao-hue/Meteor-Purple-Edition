"""Verify the distributable has real, correctly identified nested mods and source attribution."""
import io
import json
import sys
import zipfile
from pathlib import Path

artifact=sys.argv[1] if len(sys.argv)>1 else f"build/libs/meteor-purple-edition-{Path('VERSION').read_text().strip()}.jar"
with zipfile.ZipFile(artifact) as bundle:
    metadata=json.loads(bundle.read('fabric.mod.json'))
    assert metadata['id']=='meteor-purple-edition'
    nested={}
    for entry in metadata.get('jars',[]):
        data=bundle.read(entry['file'])
        with zipfile.ZipFile(io.BytesIO(data)) as mod:
            info=json.loads(mod.read('fabric.mod.json'))
            nested[info['id']]=info['version']
    assert nested.get('meteor-client')=='26.2-35',nested
    assert nested.get('catppuccin-addon')=='26.2',nested
    assert nested.get('fabric-api')=='0.154.2+26.2',nested
    assert 'glazed' not in nested
    assert 'THIRD_PARTY.md' in bundle.namelist()
    assert not any(path.startswith('net/minecraft/') for path in bundle.namelist())
    print('Verified bundle:',metadata['version'],nested)

    import hashlib, struct
    root='assets/meteor-purple-edition/textures/panorama/'
    capture=json.loads(bundle.read(root+'capture.json'))
    assert capture['minecraft']=='26.2' and len(capture['faces'])==6
    for face in capture['faces']:
        data=bundle.read(root+face['file'])
        assert data[:8]==b'\x89PNG\r\n\x1a\n'
        assert struct.unpack('>II',data[16:24])==(1024,1024)
        assert hashlib.sha256(data).hexdigest()==face['sha256']
    assert not any('moonlit-colosseum.png' in name for name in bundle.namelist())
    print('Verified six native Minecraft panorama captures and their SHA-256 hashes.')
