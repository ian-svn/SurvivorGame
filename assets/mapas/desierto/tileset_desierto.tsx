<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.11.2" name="tileset_desierto" tilewidth="32" tileheight="32" spacing="1" margin="1" tilecount="18" columns="6">
 <image source="tileset_desierto.ase" width="200" height="100"/>
 <tile id="0">
  <properties>
   <property name="destructible" type="bool" value="false"/>
   <property name="tipo" value="arena"/>
   <property name="transitable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="1">
  <properties>
   <property name="destructible" type="bool" value="false"/>
   <property name="tipo" value="arena"/>
   <property name="transitable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="2">
  <properties>
   <property name="destructible" type="bool" value="false"/>
   <property name="tipo" value="arena"/>
   <property name="transitable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="cantidadTirada" type="int" value="1"/>
   <property name="destructible" type="bool" value="true"/>
   <property name="objetoTirado" value="palo"/>
   <property name="tipo" value="arbolSeco"/>
   <property name="transitable" type="bool" value="false"/>
   <property name="vida" type="int" value="1"/>
  </properties>
 </tile>
 <tile id="6">
  <properties>
   <property name="destructible" type="bool" value="false"/>
   <property name="tipo" value="arena"/>
   <property name="transitable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="7">
  <properties>
   <property name="destructible" type="bool" value="false"/>
   <property name="tipo" value="arena"/>
   <property name="transitable" type="bool" value="true"/>
  </properties>
 </tile>
 <tile id="8">
  <properties>
   <property name="cantidadTirada" type="int" value="5"/>
   <property name="danio" type="int" value="5"/>
   <property name="destructible" type="bool" value="true"/>
   <property name="objetoTirado" value="agua"/>
   <property name="tipo" value="cactus"/>
   <property name="transitable" type="bool" value="false"/>
   <property name="vida" type="int" value="40"/>
  </properties>
 </tile>
 <tile id="9">
  <properties>
   <property name="cantidadTirada" type="int" value="4"/>
   <property name="destructible" type="bool" value="true"/>
   <property name="objetoTirado" value="piedra"/>
   <property name="tipo" value="roca"/>
   <property name="transitable" type="bool" value="false"/>
   <property name="vida" type="int" value="100"/>
  </properties>
 </tile>
</tileset>
