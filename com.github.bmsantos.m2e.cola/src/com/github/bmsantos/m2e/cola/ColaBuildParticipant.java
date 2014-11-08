package com.github.bmsantos.m2e.cola;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.sonatype.plexus.build.incremental.ThreadBuildContext.getContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class ColaBuildParticipant extends MojoExecutionBuildParticipant {

    private static final IMaven maven = MavenPlugin.getMaven();

    public ColaBuildParticipant(final MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {
        final BuildContext buildContext = getBuildContext();

        final String[] includes = getMojoParameterValue("includes", String[].class, monitor);
        final String[] excludes = getMojoParameterValue("excludes", String[].class, monitor);

        // check if any of the grammar files changed
        final File targetTestDirectory = getMojoParameterValue("targetTestDirectory", File.class, monitor);
        if (targetTestDirectory == null) { 
        	return emptySet(); 
        }

        final Scanner ds = buildContext.newScanner(targetTestDirectory); // delta or full scanner
        ds.setIncludes(includes);
        ds.setExcludes(excludes);
        ds.scan();

        final String[] includedFiles = ds.getIncludedFiles();
        if (includedFiles == null || includedFiles.length <= 0) {
            return emptySet();
        }

        getContext().setValue("colaDeltas", new ArrayList<String>(asList(includedFiles)));

        // execute mojo
        final Set<IProject> result = super.build(kind, monitor);

        // tell m2e builder to refresh compiled files
        buildContext.refresh(targetTestDirectory);

        return result;
    }

    private <T> T getMojoParameterValue(final String name, final Class<T> type, final IProgressMonitor monitor) throws CoreException {
    	final MavenProject mavenProject = getMavenProjectFacade().getMavenProject( monitor );
        return maven.getMojoParameterValue(mavenProject, getMojoExecution(), name, type, monitor);
    }
}
