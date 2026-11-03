from pathlib import Path
from PIL import Image, ImageFont, ImageDraw
root=Path(__file__).resolve().parents[1]
font=root/'src/main/resources/assets/meteor-purple-edition/font/comfortaa.ttf'
im=Image.new('RGBA',(1024,512)); d=ImageDraw.Draw(im)
def line(text,y,size,height,stroke=0):
 f=ImageFont.truetype(str(font),size)
 d.text((512,y+height/2),text,font=f,anchor='mm',fill='white',stroke_width=stroke,stroke_fill='white')
line('Zenith Client',0,138,192,1)
line('the client for the elite',192,39,80)
line('Loading',272,31,48)
f=ImageFont.truetype(str(font),62)
for i,c in enumerate('0123456789%'):
 d.text((i*64+32,360),c,font=f,anchor='mm',fill='white')
out=root/'src/main/resources/assets/meteor-purple-edition/textures/ui/brand-atlas.png'
out.parent.mkdir(parents=True,exist_ok=True); im.save(out,optimize=True)
print(out)
