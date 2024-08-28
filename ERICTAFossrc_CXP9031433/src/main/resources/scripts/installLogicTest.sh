echo -e Slave Name = `hostname`
echo -e Slave IP = `hostname -i`

if [[ "${JOB_NAME}" == *Cloud_CDB_Auto_Deployment_2* ]]; then
cdb_schedule="ossrc_schedule1.xml"
else
cdb_schedule="ossrc_schedule.xml"
fi


echo "Product: ${product}"

[ -n "${schedulerFile}" ] && cdb_schedule=${schedulerFile}
[ -z "${deployPackage}" ] && deployPackage="none"
[ -z "${scheduleVersion}" ] && scheduleVersion="RELEASE"
[ -z "${phase}" ] && phase="KGB"
[ -z "${product}" ] && product="OSS-RC"
[ -z "${isoNo}" ] && isoNo="ERICOSS-RC_CXP9022623"

#hardcoded to do install only for Virtual 3
if [[ "${JOB_NAME}" == *Cloud_CDB_Auto_Deployment_3* ]]; then
   cdb_schedule="cdb_install_only.xml"
fi


echo deployPackage=${deployPackage}
echo schedulerVersion=${scheduleVersion}


if [[ "${phase}" == *KGB* ]]; then
    echo "Package = ${deployPackage}"
    echo "Drop = ${drop}"
    echo "Schedule: ${cdb_schedule}"
    #config for multiple KGB - start
    packageList=$( echo ${deployPackage}| sed 's/||/ /g'  )
    for packageTmp in ${packageList}
    do
         echo ${packageTmp}
        #check the format of package list
        if [[ ${packageTmp} == *"|"* ]]
            then 
              echo "Please check package list."
              exit 1
        fi
        packageVersion=$( echo $packageTmp | sed 's/::/ /g' | awk '{print $2}' )
        package=$( echo $packageTmp | sed 's/::/ /g' | awk '{print $1}' ) 
        packageRevisionInfover=$( wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package=${package}&version=${packageVersion}" )
        ver=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $1}' )
        groupId=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $2}' )
        gavs=$gavs"#"$package"::"$packageRevisionInfover
        echo "Package: ${packageTmp}, groupId: ${groupId}, version: ${ver}"
    done
    echo gavs=${gavs} > /tmp/${BUILD_TAG}
    echo "GAVs = ${gavs}"
    #config for multiple KGB - end
else
    echo "Drop = ${drop}"
    echo "Schedule: ${cdb_schedule}"
echo "LLSV is ${LLSV}"
   if [[ ${LLSV} == *LLSV3*  ||  ${LLSV} == "CU" ]]; then  LLSV_media="media.txt"; else LLSV_media="media_${LLSV}.txt"; fi
fi

if [ ${CONFIG_FILES} ]; then
   config_file="${CONFIG_FILES}"
else
 config_file="/export/scripts/CLOUD/configs/templates/oss_box_dm/variables_sfs.txt:/export/scripts/CLOUD/configs/media_configs_dm/${drop}/${LLSV_media}:/export/scripts/CLOUD/configs/netsim_configs/wran_lte_gran_${drop}.txt:/export/scripts/CLOUD/configs/dm/install_options.txt"
fi

if [[ ${drop} == *15.2* ]]
then
   AOM_NUMBER="AOM 901 122"
fi

echo AOM_NUMBER=${AOM_NUMBER} >> /tmp/${BUILD_TAG}

# Declare the variables that need to be made available to the JVM
if [[ "${phase}" == *KGB* ]]; then
    # Declare the variables that need to be made available to the JVM
    # This get the first package and version from the list of packages given for the KGB install (deployPackage)
    firstPackage=$( echo $deployPackage | sed 's/||/ /g' | awk '{print $1}' )
    # Now get the package name and version as seperate variables
    packageVersion=$( echo $firstPackage | sed 's/::/ /g' | awk '{print $2}' )
    package=$( echo $firstPackage | sed 's/::/ /g' | awk '{print $1}' )

    echo "Retrieving package object for ${package} version ${packageVersion}"
    # From the package name and version above we now get the package revision information. This returns the appropriate version and the groupId of the package  
    packageRevisionInfover=$( wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package=${package}&version=${packageVersion}" )
    # Now split the returned packageRevisionInfover into the version and the groupId as seperate variables
    ver=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $1}' )
    groupId=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $2}' )
    echo "Package info : ${packageRevisionInfover}"

    # Now write all required variables to a temporary file so the environment variables can be injected into the JVM  
    echo package=$( echo ${package} )  >> /tmp/${BUILD_TAG}
    echo ver=$( echo ${ver} )  >> /tmp/${BUILD_TAG}
    echo groupId=$( echo ${groupId} )  >> /tmp/${BUILD_TAG}

    echo "Retrieving testware for package list : ${packageList}"
    testware=$( wget -q -O - --no-check-certificate "http://eselivm2v214l.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.oss.ciexec.testware&a=ERICTAFtest_CXP8888888&v=1.0.0&e=jar")
    echo testware=$( wget -q -O - --no-check-certificate "http://eselivm2v214l.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.oss.ciexec.testware&a=ERICTAFtest_CXP8888888&v=1.0.0&e=jar")  >> /tmp/${BUILD_TAG}
else
#this need update
    if [[ "$drop" == *::* ]];then
        isover=$( echo "${drop}" | sed 's/::/ /' | awk '{print $2}' )
    else
        isover=$( wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/getlatestisover/?drop=${drop}&product=${product}")
    fi
    echo isover=$( echo "${isover}" ) >> /tmp/${BUILD_TAG}
    testware=$( wget -q -O - --no-check-certificate "http://eselivm2v214l.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.oss.ciexec.testware&a=ERICTAFtest_CXP8888888&v=1.0.0&e=jar")
    echo testware=$( wget -q -O - --no-check-certificate "http://eselivm2v214l.lmera.ericsson.se:8081/nexus/service/local/artifact/maven/redirect?r=releases&g=com.ericsson.oss.ciexec.testware&a=ERICTAFtest_CXP8888888&v=1.0.0&e=jar")  >> /tmp/${BUILD_TAG}
   # productSetVer=$( wget -qO- - --no-check-certificate --post-data="" https://cifwk-oss.lmera.ericsson.se/OSS-RC/dropMedia/${drop}/createversion/) 
    #echo productSetVer=$( wget -qO- - --no-check-certificate --post-data="" https://cifwk-oss.lmera.ericsson.se/OSS-RC/dropMedia/${drop}/createversion/)  >> /tmp/${BUILD_TAG}
fi


echo "Testware : ${testware}"
#echo "productSetVer : ${productSetVer}"

#echo "Retrieving cluster data for clusterId : ${clusterId}"
#cluster_data=$( wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/generateTAFHostProperties/?clusterId=${clusterId}&format=mvn")
if [[ "${phase}" == *cloud_cdb* ]]; then
cluster_data=`cat /export/scripts/CLOUD/configs/dm/host_properties.txt`
fi



echo cluster_data="-Dhost.gateway.ip=`hostname`${cluster_data}">>/tmp/${BUILD_TAG}
#Added Selnium Grid properties

#if [[ "${phase}" == *LMS* ]]; then
 #  echo cluster_data="-Dhost.gateway.ip=`hostname` -Dhost.UiTestGrid.ip=atvts759.athtem.eei.ericsson.se -Dhost.UiTestGrid.type=SELENIUM_GRID -Dhost.UiTestGrid.port.ssh=22 -#Dhost.UiTestGrid.port.http=4441 -Dhost.UiTestGrid.user.root.pass=shroot -Dhost.UiTestGrid.user.root.type=ADMIN -Dtaf_ui.default_OS=LINUX ${cluster_data}">>/tmp/${BUILD_TAG}
#else
#echo cluster_data="-DCONFIG_FILES=${config_file} -Dhost.gateway.ip=`hostname` -Dhost.UiTestGrid.ip=`hostname`.athtem.eei.ericsson.se -Dhost.UiTestGrid.type=SELENIUM_GRID -Dhost.UiTestGrid.port.ssh=22 -Dhost.UiTestGrid.port.http=4441 -Dhost.UiTestGrid.user.root.pass=shroot -Dhost.UiTestGrid.user.root.type=ADMIN -Dtaf_ui.default_OS=LINUX ${cluster_data}">>/tmp/${BUILD_TAG}
#fi
echo cluster_data="-DCONFIG_FILES=${config_file} -Dhost.gateway.ip=`hostname` ${cluster_data}">>/tmp/${BUILD_TAG}
vAppName=`hostname`
echo "vAppName=$vAppName" >> /tmp/${BUILD_TAG}
echo "Returned cluster data : ${cluster_data}"
rm -rf pom.xml


vAppName=`hostname`



if [[ "${scheduleVersion}" == "RELEASE" ]]; then
    echo "Retrieving maven dependency com.ericsson.cifwk:ossrc-schedule:RELEASE"
           /home/ossrcdm/tools/apache-maven-3.0.4/bin/mvn -B -U org.apache.maven.plugins:maven-dependency-plugin:2.5.1:get -Dartifact=com.ericsson.cifwk:ossrc-schedule:${scheduleVersion} -Dpackaging=pom -Ddest=pom.xml
           export tafver=$(~/tools/apache-maven-3.0.4/bin/mvn -B org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version |grep -Ev '(^\[|Download\w+:)')
             rm -rf pom.xml
else
         export tafver=${scheduleVersion}
fi
echo "TAF Scheduler version = $tafver"
echo "tafver=$tafver" >> /tmp/${BUILD_TAG}

echo "Retrieving maven dependency com.ericsson.cifwk.taf:taf-run-maven-plugin:RELEASE"
/home/ossrcdm/tools/apache-maven-3.0.4/bin/mvn -B org.apache.maven.plugins:maven-dependency-plugin:2.5.1:get -Dartifact=com.ericsson.cifwk.taf:taf-run-maven-plugin:RELEASE -Dpackaging=pom -Ddest=pom.xml
export runver=$(~/tools/apache-maven-3.0.4/bin/mvn -B org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version |grep -Ev '(^\[|Download\w+:)')
rm -rf pom.xml
echo "TAF taf-run-maven-plugin version = $runver"


export manualVer=1.0.24
echo "Manual Version =${manualVer}"
echo "TAF taf-run-maven-plugin version = $manualVer"
echo "Creating pom for package ${package} with schedule file ${cdb_schedule}"
if [[ "${phase}" == *KGB* ]]; then
    packageList=$( echo ${deployPackage}| sed 's/||/%20/g'  )
    pomURL="https://cifwk-oss.lmera.ericsson.se/testware/createpom/?packagelist=${packageList}&pomversion=2.0.2&scheduleversion=${tafver}&manualversion=${manualVer}&tafrunversion=${runver}&schedulegroup=com.ericsson.cifwk&scheduleartifact=ossrc-schedule&schedulename=${cdb_schedule}"
else
    pomURL="https://cifwk-oss.lmera.ericsson.se/testware/createpom/?isoartifact=${isoNo}&isoversion=${isover}&pomversion=2.0.2&scheduleversion=${tafver}&manualversion=${manualVer}&tafrunversion=${runver}&schedulegroup=com.ericsson.cifwk&scheduleartifact=ossrc-schedule&schedulename=${cdb_schedule}"     
fi

echo "pom url : ${pomURL}"
wget -q -O - --no-check-certificate ${pomURL} > tmp.xml

echo "${phase} pom:"
xmllint --format tmp.xml>pom.xml
cat pom.xml