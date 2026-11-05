"""Reproduce bundled Fredoka Medium from the pinned original; requires fonttools==4.60.1."""
from pathlib import Path
from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont
import hashlib, json
root=Path(__file__).resolve().parents[1]
source=root/'tools/fonts/fredoka-variable.ttf'
metadata=json.loads((source.parent/'source.json').read_text())
assert hashlib.sha256(source.read_bytes()).hexdigest()==metadata['original_sha256']
font=instantiateVariableFont(TTFont(source),metadata['axes'],inplace=False)
for name in font['name'].names:
    if name.nameID in (2,17): name.string='Medium'.encode(name.getEncoding())
font['OS/2'].usWeightClass=500
font.save(root/'src/main/resources/assets/meteor-purple-edition/font/fredoka.ttf')
