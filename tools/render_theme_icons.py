"""Draw the original Zenith icon family. Ten silhouettes/treatments, one compact atlas per theme.
All geometry is authored here; no third-party icon package is copied. Requires Pillow.
"""
from pathlib import Path
from PIL import Image, ImageDraw
import math,json,hashlib
R=Path(__file__).resolve().parents[1]; OUT=R/'src/main/resources/assets/meteor-purple-edition/textures/icons'
NAMES='arrow bookmark_no bookmark_yes brush copy cube edit eye grid import link minus mouse movement people person plus question_mark reset search setting sword tick logo globe accessibility'.split()
STYLES=[('mocha','#c49aff',0),('glass','#dab8ff',1),('minimal','#bba8e8',2),('os26','#7952cc',3),('midnight','#a69aff',4),('rose','#f1aacd',5),('ocean','#87daf0',6),('forest','#9fdfc1',7),('amoled','#c6a4ff',8),('original','#b66bff',9)]
S=4; TILE=48

def make(name,accent,style):
 im=Image.new('RGBA',(TILE*S,TILE*S)); d=ImageDraw.Draw(im)
 rgb=tuple(bytes.fromhex(accent[1:])); white=(249,246,255,255)
 color=rgb+(255,); soft=rgb+(72,)
 # Distinct per-theme language: floating tiles, glass lozenges, line icons, pearl discs,
 # neon cut-corner frames, rose capsules, tidal arcs, leaf corners, mono and pixel frames.
 def box(b,fill,r=0,outline=None,w=1):
  b=tuple(int(v*S) for v in b)
  if r: d.rounded_rectangle(b,radius=int(r*S),fill=fill,outline=outline,width=max(1,int(w*S)))
  else: d.rectangle(b,fill=fill,outline=outline,width=max(1,int(w*S)))
 def ellipse(b,fill=None,outline=None,w=1): d.ellipse(tuple(int(v*S) for v in b),fill=fill,outline=outline,width=max(1,int(w*S)))
 def line(p,fill=color,w=2.2):
  pts=[(int(x*S),int(y*S)) for x,y in p]; d.line(pts,fill=fill,width=max(1,int(w*S)),joint='curve')
  if style not in (4,8,9):
   for x,y in (p[0],p[-1]): ellipse((x-w/2,y-w/2,x+w/2,y+w/2),fill)
 def poly(p,fill=soft): d.polygon([(int(x*S),int(y*S)) for x,y in p],fill=fill)
 if style==0: box((3,3,45,45),rgb+(25,),12); box((8,5,39,6),rgb+(55,),1)
 elif style==1: box((2,2,46,46),rgb+(27,),14, rgb+(68,),.8); line([(12,6),(31,6)],white[:3]+(75,),1)
 elif style==3: ellipse((2,2,46,46),(251,248,255,235),rgb+(70,),.8); ellipse((7,5,34,20),(255,255,255,90))
 elif style==4: poly([(4,10),(10,4),(44,4),(44,38),(38,44),(4,44)],rgb+(15,)); line([(4,17),(4,10),(10,4),(23,4)],rgb+(95,),1)
 elif style==5: box((3,5,45,43),rgb+(28,),16); ellipse((8,9,14,15),rgb+(90,))
 elif style==6: d.arc((2*S,2*S,46*S,46*S),25,150,fill=rgb+(110,),width=S); d.arc((2*S,2*S,46*S,46*S),205,330,fill=rgb+(55,),width=S)
 elif style==7: poly([(4,15),(4,5),(15,5),(9,11)],rgb+(65,)); poly([(44,33),(44,43),(33,43),(39,37)],rgb+(65,))
 elif style==9: box((4,4,44,44),rgb+(22,),0); box((4,4,6,44),rgb+(90,))
 c=color; w=1.7 if style in (2,6,7,8) else 2.3
 if style==9: w=2.4
 # Core symbols stay recognizable while chrome/stroke/duotone varies by theme.
 if name=='sword':
  poly([(16,28),(30,12),(36,12),(36,18),(22,32)],soft); line([(16,28),(30,12),(36,12),(36,18),(22,32)],c,w); line([(12,25),(25,38)],c,w); line([(18,31),(12,37)],c,w)
 elif name in ('person','accessibility'):
  ellipse((20,10,28,18),c if style not in (2,8) else None,c,w)
  if name=='person': line([(14,36),(14,29),(19,24),(29,24),(34,29),(34,36),(14,36)],c,w)
  else: line([(11,23),(24,25),(37,23)],c,w); line([(24,24),(24,30),(17,38)],c,w); line([(24,30),(31,38)],c,w)
 elif name=='people':
  ellipse((20,10,28,18),c); ellipse((9,16,15,22),soft,c,w); ellipse((33,16,39,22),soft,c,w); line([(16,36),(16,28),(21,24),(27,24),(32,28),(32,36)],c,w); line([(8,34),(8,29),(12,26)],c,w); line([(40,34),(40,29),(36,26)],c,w)
 elif name=='movement': line([(10,27),(27,10),(22,24),(36,21),(19,38),(23,27),(10,27)],c,w)
 elif name=='eye':
  line([(8,24),(15,16),(24,13),(33,16),(40,24),(33,32),(24,35),(15,32),(8,24)],c,w); ellipse((18,18,30,30),soft,c,w); ellipse((22,22,26,26),c)
 elif name=='cube':
  poly([(24,9),(38,17),(38,32),(24,40),(10,32),(10,17)],soft); line([(24,9),(38,17),(38,32),(24,40),(10,32),(10,17),(24,9)],c,w); line([(10,17),(24,25),(38,17)],c,w); line([(24,25),(24,40)],c,w)
 elif name=='grid':
  for x,y in [(12,12),(27,12),(12,27),(27,27)]: box((x,y,x+9,y+9),soft,2,c,w)
 elif name=='setting':
  for y,x in [(14,19),(24,30),(34,22)]: line([(10,y),(38,y)],c,w); ellipse((x-3,y-3,x+3,y+3),rgb+(255,),white if style in (1,3) else c,w)
 elif name=='search': ellipse((10,9,31,30),soft,c,w); line([(29,28),(39,38)],c,w+0.3)
 elif name=='arrow': line([(14,19),(24,29),(34,19)],c,w)
 elif name in ('plus','minus'):
  line([(13,24),(35,24)],c,w)
  if name=='plus': line([(24,13),(24,35)],c,w)
 elif name=='tick': line([(12,24),(21,32),(37,15)],c,w+1)
 elif name in ('bookmark_no','bookmark_yes'):
  p=[(15,10),(33,10),(33,38),(24,32),(15,38),(15,10)]; poly(p,soft if name.endswith('no') else rgb+(150,)); line(p,c,w)
 elif name=='copy': box((10,16,30,38),soft,3,c,w); line([(18,11),(37,11),(37,31)],c,w)
 elif name in ('edit','brush'):
  poly([(13,31),(30,12),(36,18),(19,36),(12,38)],soft); line([(13,31),(30,12),(36,18),(19,36),(12,38),(13,31)],c,w); line([(27,16),(32,21)],c,w)
 elif name=='import': line([(12,27),(12,36),(36,36),(36,27)],c,w); line([(24,10),(24,29),(17,22)],c,w); line([(24,29),(31,22)],c,w)
 elif name=='link':
  line([(26,15),(32,10),(39,17),(31,26),(27,27)],c,w); line([(22,33),(16,38),(9,31),(17,22),(21,21)],c,w); line([(18,30),(30,18)],c,w)
 elif name=='reset':
  d.arc((11*S,11*S,37*S,37*S),-45,250,fill=c,width=max(1,int(w*S))); line([(11,12),(11,22),(21,22)],c,w)
 elif name=='mouse': box((14,9,34,39),soft,10,c,w); line([(24,10),(24,22)],c,w); line([(15,22),(33,22)],c,w)
 elif name=='globe':
  ellipse((9,9,39,39),soft,c,w); ellipse((18,9,30,39),None,c,w); line([(10,24),(38,24)],c,w); line([(13,15),(35,15)],c,w); line([(13,33),(35,33)],c,w)
 elif name=='question_mark':
  d.arc((16*S,11*S,32*S,25*S),170,450,fill=c,width=int(w*S)); line([(24,25),(24,29)],c,w); ellipse((22.5,34,25.5,37),c)
 elif name=='logo':
  for p in [[(8,12),(11,10),(24,17),(21,20)],[(8,17),(8,20),(21,27),(21,24)],[(14,9),(17,7),(29,14),(26,17)],[(26,23),(35,18),(43,23),(34,29)],[(26,23),(34,29),(34,39),(26,34)],[(34,29),(43,23),(43,34),(34,39)]]: poly(p,c if p[0][0] in (8,14,26) else rgb+(150,))
 return im.resize((TILE,TILE),Image.Resampling.LANCZOS)

OUT.mkdir(parents=True,exist_ok=True); manifest={'tile':TILE,'columns':6,'rows':5,'names':NAMES,'themes':{}}
for name,accent,style in STYLES:
 atlas=Image.new('RGBA',(TILE*6,TILE*5))
 for i,icon in enumerate(NAMES): atlas.alpha_composite(make(icon,accent,style),((i%6)*TILE,(i//6)*TILE))
 out=OUT/(name+'.png'); atlas.save(out,optimize=True)
 manifest['themes'][name]={'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'style':style}
(OUT/'icons.json').write_text(json.dumps(manifest,indent=2)+'\n')
preview=Image.new('RGB',(TILE*len(NAMES),TILE*len(STYLES)),(20,16,29))
for row,(name,accent,style) in enumerate(STYLES):
 for col,icon in enumerate(NAMES): preview.paste(make(icon,accent,style),(col*TILE,row*TILE),make(icon,accent,style))
preview.save(R/'docs/theme-icons.png')
print('Generated 260 original themed symbols in ten atlases.')
