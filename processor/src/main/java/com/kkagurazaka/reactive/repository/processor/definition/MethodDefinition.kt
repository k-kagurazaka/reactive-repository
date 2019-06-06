package com.kkagurazaka.reactive.repository.processor.definition

import com.kkagurazaka.reactive.repository.processor.ProcessingContext
import com.kkagurazaka.reactive.repository.processor.exception.ProcessingException
import com.kkagurazaka.reactive.repository.processor.tools.Types
import com.kkagurazaka.reactive.repository.processor.tools.isNonNullAnnotated
import com.kkagurazaka.reactive.repository.processor.tools.isNullableAnnotated
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ExecutableElement

class MethodDefinition<ED : EntityDefinition<out Annotation>>(
    context: ProcessingContext,
    val element: ExecutableElement,
    val entityDefinition: ED
) {

    val methodName: String
    val type: Type

    init {
        methodName = element.simpleName.toString()

        val entityClassName = entityDefinition.className
        val isEmptyParameter = element.parameters.isEmpty()
        val returnTypeName = TypeName.get(element.returnType)

        type = when {
            isEmptyParameter -> when {
                // getter has no parameters and returns entity class
                returnTypeName == entityClassName -> {
                    when {
                        element.isNullableAnnotated -> Type.NullableGetter
                        element.isNonNullAnnotated -> Type.NonNullGetter
                        else -> Type.PlatFormTypeGetter
                    }
                }
                // method returning Rx Observable has no parameters and return Observable<Entity>
                returnTypeName is ParameterizedTypeName && returnTypeName.rawType == Types.rx2Observable -> {
                    when {
                        element.isNullableAnnotated -> {
                            throw ProcessingException(
                                "Method returning Observable cannot be annotated with @Nullable",
                                element
                            )
                        }
                        returnTypeName == ParameterizedTypeName.get(Types.rx2Observable, entityClassName) -> {
                            Type.Rx2Observable
                        }
                        else -> {
                            val typeParameter = returnTypeName.typeArguments.single()
                            throw ProcessingException(
                                "Expected return type is Observable<$entityClassName> but actual is Observable<$typeParameter> at ${element.simpleName}()",
                                element
                            )
                        }
                    }
                }
                // method returning Rx Flowable has no parameters and return Flowable<Entity>
                returnTypeName is ParameterizedTypeName && returnTypeName.rawType == Types.rx2Flowable -> {
                    when {
                        element.isNullableAnnotated -> {
                            throw ProcessingException(
                                "Method returning Flowable cannot be annotated with @Nullable",
                                element
                            )
                        }
                        returnTypeName == ParameterizedTypeName.get(Types.rx2Flowable, entityClassName) -> {
                            Type.Rx2Flowable
                        }
                        else -> {
                            val typeParameter = returnTypeName.typeArguments.single()
                            throw ProcessingException(
                                "Expected return type is Flowable<$entityClassName> but actual is Flowable<$typeParameter> at ${element.simpleName}()",
                                element
                            )
                        }
                    }
                }
                else -> {
                    throw ProcessingException(
                        "Expected return type is $entityClassName, Observable<$entityClassName> or Flowable<$entityClassName> but actual is $returnTypeName at ${element.simpleName}()",
                        element
                    )
                }
            }
            // setter take entity class as parameter and returns void
            element.parameters.size == 1 && ClassName.get(element.parameters.single().asType()) == entityClassName -> {
                val parameter = element.parameters.single()
                val name = parameter.simpleName.toString()
                when {
                    parameter.isNullableAnnotated -> Type.NullableSetter(name)
                    parameter.isNonNullAnnotated -> Type.NonNullSetter(name)
                    else -> Type.PlatFormTypeSetter(name)
                }
            }
            else -> {
                throw ProcessingException("Signature of ${element.simpleName} is not supported", element)
            }
        }
    }

    sealed class Type {
        object NullableGetter : Type()
        object NonNullGetter : Type()
        object PlatFormTypeGetter : Type()
        object Rx2Observable : Type()
        object Rx2Flowable : Type()
        data class NullableSetter(val parameterName: String) : Type()
        data class NonNullSetter(val parameterName: String) : Type()
        data class PlatFormTypeSetter(val parameterName: String) : Type()
    }
}
