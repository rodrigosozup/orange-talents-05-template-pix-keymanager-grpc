package br.com.keymanagergrpc.pix.registra

import io.micronaut.core.annotation.AnnotationValue
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = []) //ValidPixKeyValidator::class
annotation class ValidPixKey(
    val message: String = "chave Pix inválida (\${validatedValue.tipo})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)
//Rever. Não entendi a impleplentação.
/*
@Singleton
class ValidPixKeyValidator : javax.validation.ConstraintValidator<ValidPixKey, NovaChavePix> {

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext?): Boolean {

        // must be validated with @NotNull
        if (value?.tipo == null) {
            return true
        }

        return value.tipo.valida(value.chave)

    }
}
 */

