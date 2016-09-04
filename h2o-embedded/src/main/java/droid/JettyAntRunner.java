package droid;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class JettyAntRunner {

	public static void run(String antScript, String target) {
		File antScriptFile = new File(antScript);		
		Project project = new Project();
		project.init();
		ProjectHelper.getProjectHelper().parse(project, antScriptFile);
		if (target != null)
			project.executeTarget(target);
		else
			project.executeTarget(project.getDefaultTarget());
	}
}
