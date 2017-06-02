package org.camunda.bpm.engine.test.api.variables;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.util.ProcessEngineTestRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

public class ModifyVariableInSameTransactionTest {
  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule();
  @Rule
  public ProcessEngineTestRule testHelper = new ProcessEngineTestRule(engineRule);

  @Test
  public void testDeleteAndInsertTheSameVariable() {

    BpmnModelInstance bpmnModel =
        Bpmn.createExecutableProcess("serviceTaskProcess")
        .startEvent()
        .userTask("userTask")
        .serviceTask("service")
          .camundaClass(DeleteAndInsertVariableDelegate.class)
        .userTask("userTask1")
        .endEvent()
        .done();
    ProcessDefinition processDefinition = testHelper.deployAndGetDefinition(bpmnModel);
    VariableMap variables = Variables.createVariables().putValue("listVar", Arrays.asList(new int[] { 1, 2, 3 }));
    ProcessInstance instance = engineRule.getRuntimeService().startProcessInstanceById(processDefinition.getId(), variables);

    Task task = engineRule.getTaskService().createTaskQuery().singleResult();
    engineRule.getTaskService().complete(task.getId());

    Object variableValue = engineRule.getRuntimeService().getVariable(instance.getId(), "listVar");
    assertNotNull(variableValue);
  }

}
