package com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.features;

import com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.JobView;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.smartcodeltd.jenkinsci.plugins.buildmonitor.viewmodel.syntacticsugar.Sugar.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.net.URL;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Jenkins.class, URL.class})
public class HasBadgesGroovyPostbuildPluginTest {
    private JobView job;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Jenkins jenkins;

    @Before
    public void setup() {
        PowerMockito.mockStatic(Jenkins.class);
        PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
    }

    @Test
    public void should_support_job_without_badges() throws Exception {
        job = a(jobView().which(new HasBadgesGroovyPostbuildPlugin()).of(
                a(job())));

        assertThat(serialisedBadgesDetailsOf(job), is(nullValue()));
    }

    @Test
    public void should_convert_badges_to_json() throws Exception {
        job = a(jobView().which(new HasBadgesGroovyPostbuildPlugin()).of(
            a(job().whereTheLast(build().hasBadgesGroovyPostbuildPlugin(groovyPostbuildBadge().withText("badge1"), groovyPostbuildBadge().withText("badge2"))))));

        assertThat(serialisedBadgesDetailsOf(job).value(), hasSize(2));
    }

    @Test
    public void should_ignore_badges_with_icon() throws Exception {
        job = a(jobView().which(new HasBadgesGroovyPostbuildPlugin()).of(
            a(job().whereTheLast(build().hasBadgesGroovyPostbuildPlugin(groovyPostbuildBadge().withIcon("icon.gif", "badge1"), groovyPostbuildBadge().withText("badge2"))))));

        assertThat(serialisedBadgesDetailsOf(job).value(), hasSize(1));
    }

    @Test
    public void should_report_badges_from_latest_build() throws Exception {
        job = a(jobView().which(new HasBadgesGroovyPostbuildPlugin()).of(
                a(job().whereTheLast(build().isStillBuilding().hasBadgesGroovyPostbuildPlugin(groovyPostbuildBadge().withText("badge1")))
                        .andThePrevious(build().hasBadgesGroovyPostbuildPlugin(groovyPostbuildBadge().withText("badge1"), groovyPostbuildBadge().withText("badge2"))))));

        assertThat(serialisedBadgesDetailsOf(job).value(), hasSize(1));
    }

    private HasBadgesGroovyPostbuildPlugin.Badges serialisedBadgesDetailsOf(JobView job) {
        return job.which(HasBadgesGroovyPostbuildPlugin.class).asJson();
    }
}
