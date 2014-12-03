package com.github.bmsantos.m2e.cola;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectUtils;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;

public class ColaProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {

    @Override
    protected IPath getFullPath(final IMavenProjectFacade facade, final File file) {
        final IProject project = facade.getProject();
        if (file == null) {
            return null;
        }
        final IPath path = MavenProjectUtils.getProjectRelativePath(project, file.getAbsolutePath());
        return project.getFullPath().append(path);
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(
        final IMavenProjectFacade projectFacade, final MojoExecution execution,
        final IPluginExecutionMetadata executionMetadata) {
        return new ColaBuildParticipant(execution);
    }
}
