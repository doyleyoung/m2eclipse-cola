package com.github.bmsantos.m2e.cola.tests;

import static org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.INCREMENTAL_BUILD;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

@SuppressWarnings("restriction")
public class ColaCompileMojoTest extends AbstractMavenProjectTestCase {

    private static final String TARGET_DIR = "projects/cola/cola-v1/target/test-classes/";
    private static final String TEST_CLASS = "com/github/bmsantos/maven/cola/ColaTest.class";

    public void testShouldExecuteColaTestsV1() throws Exception {

        // Given
        final ResolverConfiguration configuration = new ResolverConfiguration();
        final IProject project;
        try {
            project = importProject("projects/cola/cola-v1/pom.xml", configuration);
        } catch (final Exception t) {
            throw t;
        }
        waitForJobsToComplete();

        project.build(FULL_BUILD, monitor);
        project.build(INCREMENTAL_BUILD, monitor);
        waitForJobsToComplete();

        assertNoErrors(project);

        final IJavaProject javaProject = JavaCore.create(project);
        javaProject.getRawClasspath();

        assertTrue(project.getFile(TARGET_DIR + TEST_CLASS).isAccessible());

        final URLClassLoader cl = new URLClassLoader(new URL[] { new URL(TARGET_DIR) });
        final Class<?> colaTest = cl.loadClass(TEST_CLASS.replace("/", ".").replace(".class", ""));
        final Method scenario1 = colaTest.getMethod("Should add two numbers", new Class<?>[] {});
        cl.close();

        assertThat(scenario1, notNullValue());
    }

}
