#!/system/xbin/sh

echo "正在安装"

busybox unzip /data/data/com.slc.qemu/SLCQ.zip -d /data/data/com.slc.qemu/files
echo "正在设置权限"
busybox chmod -R ug+rwx  /data/data/com.slc.qemu/files/qemu
echo "正在安装busybox"
/data/data/com.slc.qemu/files/qemu/bin/busybox --install -s /data/data/com.slc.qemu/files/qemu/bin
echo "安装完成"
