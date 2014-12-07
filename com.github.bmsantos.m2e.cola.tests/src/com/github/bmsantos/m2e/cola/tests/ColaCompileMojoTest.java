package com.github.bmsantos.m2e.cola.tests;

import static java.io.File.separator;
import static org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.INCREMENTAL_BUILD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.objectweb.asm.ClassReader;

import com.github.bmsantos.m2e.cola.tests.utils.MethodCollector;

@SuppressWarnings("restriction")
public class ColaCompileMojoTest extends AbstractMavenProjectTestCase {

    private static final String COLA_MAVEN_PROJECT = "projects/cola/cola-v1/pom.xml";
    private static final String TEST_CLASS = "com.github.bmsantos.maven.cola.ColaTest";

    public void 
    testShouldExecuteColaTestsV1() 
        throws Exception {
        try {
            // Given - When
            final IProject project = loadColaMavenProject(COLA_MAVEN_PROJECT);

            final List<String> methods = retrieveClassMethods(project, TEST_CLASS);

            // Then
            assertTrue(methods.contains("Introduce addition : Should add two numbers"));
            assertTrue(methods.contains("Introduce addition : Should add two numbers again"));
        } catch (final Exception e) {
            System.err.println("Error: " + e.getMessage() + " : " + e.getCause());
        }
    }

    private List<String>
    retrieveClassMethods(final IProject project, final String binaryClassName)
        throws MalformedURLException, FileNotFoundException, IOException {
        final MethodCollector collector = new MethodCollector();

        final IFile classPath = project.getFile("target/test-classes");
        final String targetTestDir = classPath.getLocationURI().toURL().toString().replace("file:/", separator);

        final File f = new File(targetTestDir + separator + binaryClassName.replace(".", separator) + ".class");
        final InputStream in = new FileInputStream(f);
        final ClassReader classReader = new ClassReader(in);

        classReader.accept(collector, 0);
        return collector.getMethods();
    }

    private IProject
    loadColaMavenProject(final String projectPath)
        throws IOException, CoreException, InterruptedException, JavaModelException {

        final ResolverConfiguration configuration = new ResolverConfiguration();
        final IProject project = importProject(projectPath, configuration);
        waitForJobsToComplete();

        project.build(FULL_BUILD, monitor);
        project.build(INCREMENTAL_BUILD, monitor);
        waitForJobsToComplete();

        assertNoErrors(project);

        final IJavaProject javaProject = JavaCore.create(project);
        javaProject.getRawClasspath();
        return project;
    }
}