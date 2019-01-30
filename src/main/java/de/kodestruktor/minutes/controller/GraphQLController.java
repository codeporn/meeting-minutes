package de.kodestruktor.minutes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

@RestController
public class GraphQLController {

  @Autowired
  private GraphQL graphQL;

/**
 * @author Christoph Wende
 */
@PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public Map<String, Object> indexFromAnnotated(@RequestBody Map<String, Object> request, HttpServletRequest raw) {
  ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput()
          .query(request.get("query").toString())
          .operationName(request.get("operationName") != null ? request.get("operationName").toString() : null)
          .context(raw)
          .build());
  return executionResult.toSpecification();
}}