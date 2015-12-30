#!/bin/bash

echo "Before to continue is necessary to have installed mysql"
read -p "Do you want to continue (Y/n): " continuar

if [ $continuar != "Y" ];
then
	exit
fi 

echo "Copying files..."
sudo mkdir /opt/softlab
sudo mkdir /opt/softlab/reportes

sudo cp -R image /opt/softlab/
sudo cp -R master_report /opt/softlab/
sudo cp SoftLab/dist/SoftLab1.2.jar /opt/softlab/
sudo cp softlab /opt/softlab/

#sudo chown  
sudo chmod 755 /opt/softlab/master_report
sudo chmod 755 /opt/softlab/reportes
sudo chmod 555 /opt/softlab/SoftLab1.2.jar
sudo chmod 555 /opt/softlab/lib
sudo chmod 555 /opt/softlab/softlab

sudo ln -s /opt/softlab/reportes ~/
sudo ln -s /opt/softlab/softlab /usr/bin/softlab
echo "Copy finished"

echo "Dowload libraries.."
mkdir lib
cd lib
wget archive.apache.org/dist/commons/codec/binaries/commons-codec-1.8-bin.tar.gz
tar -xvf commons-codec-1.8-bin.tar.gz
mv commons-codec-1.8/commons-codec-1.8.jar ./
rm -R commons-codec-1.8
rm commons-codec-1.8-bin.tar.gz

wget www.java2s.com/Code/JarDownload/commons-collections/commons-collections-2.1.1.jar.zip
unzip commons-collections-2.1.1.jar.zip
rm commons-collections-2.1.1.jar.zip

wget www.java2s.com/Code/JarDownload/commons-digester/commons-digester-1.7.jar.zip
unzip commons-digester-1.7.jar.zip
rm commons-digester-1.7.jar.zip

wget www.java2s.com/Code/JarDownload/commons-logging/commons-logging-1.1.jar.zip
unzip commons-logging-1.1.jar.zip
rm commons-logging-1.1.jar.zip

wget www.java2s.com/Code/JarDownload/groovy-all/groovy-all-1.7.5.jar.zip
unzip groovy-all-1.7.5.jar.zip
rm groovy-all-1.7.5.jar.zip

wget http://mirrors.ibiblio.org/pub/mirrors/maven2/com/lowagie/itext/2.1.7/itext-2.1.7.jar

wget www.java2s.com/Code/JarDownload/jasperreports/jasperreports-4.5.0.jar.zip
unzip jasperreports-4.5.0.jar.zip
rm jasperreports-4.5.0.jar.zip

wget repo1.maven.org/maven2/org/jfree/jcommon/1.0.17/jcommon-1.0.17.jar

wget https://find-ur-pal.googlecode.com/files/mysql-connector-java-5.1.18-bin.jar

cd ..
sudo mv lib /opt/softlab/
sudo cp LibLab/dist/LibLab.jar /opt/softlab/lib
echo "Dowload finished"

echo "Creating Database..."
read -p "name user database: " user
mysql -u $user -p$passw < laboratorio.sql
if [ $? = 0 ]; then 
	echo "installation finished"
else
	echo "failure to creating database"
fi

