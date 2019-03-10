#@IgnoreInspection BashAddShebang
gource -s 1 -a 1 -1920x1080 -o - -r 30 --user-image-dir "E:/Documents/gource-users" | ffmpeg -y -i pipe:0 -f mp4 "E:/Videos/RawMaterial/gource.mp4"
read -n1 -r -p "Press any key to exit..." key