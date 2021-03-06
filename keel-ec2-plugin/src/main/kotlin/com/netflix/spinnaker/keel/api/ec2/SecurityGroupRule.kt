/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.keel.api.ec2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes(
  Type(value = SelfReferenceRule::class, name = "self-ref"),
  Type(value = ReferenceRule::class, name = "ref"),
  Type(value = CrossAccountReferenceRule::class, name = "x-account-ref"),
  Type(value = CidrRule::class, name = "CIDR")
)
@JsonInclude(NON_NULL)
sealed class SecurityGroupRule {
  abstract val protocol: Protocol
  abstract val portRange: PortRange

  enum class Protocol {
    TCP, UDP, ICMP
  }
}

data class SelfReferenceRule(
  override val protocol: Protocol,
  override val portRange: PortRange
) : SecurityGroupRule()

data class ReferenceRule(
  override val protocol: Protocol,
  val name: String,
  override val portRange: PortRange
) : SecurityGroupRule() {
  constructor(protocol: Protocol, reference: SecurityGroup, portRange: PortRange) : this(
    protocol = protocol,
    name = reference.name,
    portRange = portRange
  )
}

data class CrossAccountReferenceRule(
  override val protocol: Protocol,
  val name: String,
  val account: String,
  val vpcName: String,
  override val portRange: PortRange
) : SecurityGroupRule() {
  constructor(protocol: Protocol, reference: SecurityGroup, portRange: PortRange) : this(
    protocol = protocol,
    name = reference.name,
    account = reference.accountName,
    vpcName = reference.vpcName!!,
    portRange = portRange
  )
}

data class CidrRule(
  override val protocol: Protocol,
  override val portRange: PortRange,
  val blockRange: String
) : SecurityGroupRule()

data class PortRange(
  val startPort: Int,
  val endPort: Int
)
