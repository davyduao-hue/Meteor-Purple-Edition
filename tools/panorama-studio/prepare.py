"""Fetch only pinned official capture dependencies into this isolated studio."""
from pathlib import Path
from urllib.request import Request,urlopen
import hashlib, shutil
root=Path(__file__).resolve().parent
files=[
 ("mods/iris.jar","https://cdn.modrinth.com/data/YL57xq9U/versions/gxZWWnKH/iris-fabric-1.11.4%2Bmc26.2.jar","dee955580976aebc92373f5c2d88398ad1019b043c6254a41fe7ab8a6ca3a5ff29d58df2b22ec43a31198beb769e814658eef06fbf980066a2e2c216eeafd32a"),
 ("mods/sodium.jar","https://cdn.modrinth.com/data/AANobbMI/versions/xJZxADzI/sodium-fabric-0.9.2%2Bmc26.2.jar","9b7a7aade8824543ce2a804fddfd04e37f57d1f91c00574e7dc87e554eb1835d888313ee6f715a7599bd729840cf01fd74e02aeae87b4e5a3c10c34a016e407c"),
 ("shaderpacks/ComplementaryReimagined_r5.9.3.zip","https://cdn.modrinth.com/data/HVnmMxH1/versions/Bqen1mJX/ComplementaryReimagined_r5.9.3.zip","45304b1d7862afdb2177b7e6226fc9c6737911d17a4ad46cc9c519ca4deb935f05072cd337cd8b19a15bb8062b57b54eceec7bdee4cd572d94858e2946d37d85")]
for name,url,digest in files:
 path=root/'run'/name; path.parent.mkdir(parents=True,exist_ok=True)
 if not path.exists() or hashlib.sha512(path.read_bytes()).hexdigest()!=digest:
  data=urlopen(Request(url,headers={'User-Agent':'ZenithPanoramaStudio/1.0'}),timeout=60).read()
  if hashlib.sha512(data).hexdigest()!=digest: raise ValueError('Checksum mismatch: '+name)
  path.write_bytes(data)
 print('Verified',name)
config=root/'run/config'; config.mkdir(parents=True,exist_ok=True)
(config/'iris.properties').write_text('enableShaders=true\nshaderPack=ComplementaryReimagined_r5.9.3.zip\n',encoding='utf-8')
shutil.copyfile(root/'capture-settings.txt',root/'run/shaderpacks/ComplementaryReimagined_r5.9.3.zip.txt')
