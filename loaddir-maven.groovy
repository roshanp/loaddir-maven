import com.google.common.io.Files;

@Grab(group='com.google.guava', module='guava', version='11.0.1')
class LoadDirMvnOut {
	
	def artifactsList
	def artifactsDir
	def outputMavenDir
	def props = new Properties()
	
	def init() {
		//assertions
		assert artifactsList != null
		assert artifactsDir != null
		assert outputMavenDir != null
		
		//load artifacts list
		props.load(new FileInputStream(artifactsList))
		
		//list through libraries
		def adir = new File(artifactsDir)
		def outdir = new File(outputMavenDir)
		assert adir.exists()
		assert adir.isDirectory()
		
		adir.listFiles().each { f ->
			if(!f.isDirectory()) {	//no subdir recursion
				def fname = f.name
				def versionSplitIndex = fname.lastIndexOf(".")
				if(versionSplitIndex != -1) {
					def artifactId = fname.substring(0, versionSplitIndex)
					def fullMvnUrl = props.getProperty(artifactId)
					if(fullMvnUrl == null) {
						println "WARNING: ${artifactId} not found in the artifact list"
						return;
					}
					
					def outFile = new File(translateFromMaven(fullMvnUrl))
					Files.createParentDirs(outFile)
					Files.copy(f, outFile)
				} else {
					println "WARNING: ${fname} is not in the correct format: artifactId-version.jar"
					return;
				}
			}
		}
		
	}
	
	def translateFromMaven(String uri) {
        if (uri.startsWith("mvn:")) {
            String[] parts = uri.substring("mvn:".length()).split("/");
            String groupId = parts[0];
            String artifactId = parts[1];
            String version = null;
            String classifier = null;
            String type = "jar";
            if (parts.length > 2) {
                version = parts[2];
                if (parts.length > 3) {
                    type = parts[3];
                    if (parts.length > 4) {
                        classifier = parts[4];
                    }
                }
            }
            
            String dir = groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/";
            String name = artifactId + "-" + version + (classifier != null ? "-" + classifier : "") + "." + type;

            return outputMavenDir + "/" + dir + name;
        }
        if (System.getProperty("os.name").startsWith("Windows")
                && uri.startsWith("file:")) {
            String baseDir = uri.substring(5).replace('\\', '/').replaceAll(
                    " ", "%20");
            String result = baseDir;
            if (baseDir.indexOf(":") > 0) {
                result = "file:///" + baseDir;
            }
            return result;
        }
        return uri;
    }
	
}

assert args.length == 3 : "Usage: groovy DeployDirMvnArtifacts <maven artifacts list> <input lib dir> <output maven dir>"

def artifactsList = args[0]
def artifactsDir = args[1]
def outputMavenDir = args[2]

def readDir = new LoadDirMvnOut(artifactsList: artifactsList, artifactsDir: artifactsDir, outputMavenDir: outputMavenDir)
readDir.init()

