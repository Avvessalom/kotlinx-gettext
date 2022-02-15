/*
 * Copyright 2022 Victor Kropp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kropp.kotlinx.gettext

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.util.IdSignature

class KeywordSpec(
    private val keyword: String,
    private val args: List<KeywordSpecArgument>
) {
    constructor(spec: String) : this(spec.substringBefore(':'), spec.substringAfter(':', "").let { if (it.isEmpty()) emptyList() else it.split(',').map { KeywordSpecArgument(it) } })

    fun matches(expression: IrCall): Boolean {
        return (expression.symbol.signature as? IdSignature.CommonSignature)?.shortName == keyword
    }

    fun process(expression: IrCall, reference: String): MsgId? {
        when(args.size) {
            0 -> {
                val text = getStringOrNull(expression, 0) ?: return null
                return MsgId(listOf(reference), null, text, null)
            }
            2 -> {
                return if (args[0].isContext) {
                    val text = getStringOrNull(expression, args[1].index - 1) ?: return null
                    MsgId(listOf(reference), getStringOrNull(expression, args[0].index - 1), text, null)
                } else {
                    val text = getStringOrNull(expression, args[0].index - 1) ?: return null
                    MsgId(listOf(reference), null, text, getStringOrNull(expression, args[1].index - 1))
                }
            }
            else -> {
                return null
            }
        }
    }

    private fun getStringOrNull(expression: IrCall, index: Int): String? {
        val valueArgument = expression.getValueArgument(index)
        if (valueArgument is IrConst<*> && valueArgument.kind == IrConstKind.String) {
            @Suppress("UNCHECKED_CAST")
            return (valueArgument as IrConst<String>).value
        }
        return null
    }
}

