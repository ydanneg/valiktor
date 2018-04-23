package org.valiktor

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.valiktor.ValidatorFixture.Address
import org.valiktor.ValidatorFixture.City
import org.valiktor.ValidatorFixture.Company
import org.valiktor.ValidatorFixture.Country
import org.valiktor.ValidatorFixture.Dependent
import org.valiktor.ValidatorFixture.Employee
import org.valiktor.ValidatorFixture.State
import org.valiktor.constraints.*
import org.valiktor.functions.*
import org.valiktor.i18n.DefaultI18nConstraintViolation
import org.valiktor.i18n.I18nConstraintViolation
import org.valiktor.i18n.mapToI18n
import java.util.*

private object Locales {

    val DEFAULT = Locale("")
    val EN = Locale("en")
    val PT_BR = Locale("pt", "BR")
}

private object ValidatorFixture {

    val supportedLocales = setOf(
            Locales.DEFAULT,
            Locales.EN,
            Locales.PT_BR)

    data class Employee(val id: Int? = null,
                        val name: String? = null,
                        val email: String? = null,
                        val username: String? = null,
                        val company: Company? = null,
                        val address: Address? = null,
                        val dependents: Array<Dependent>? = null)

    data class Dependent(val id: Int? = null,
                         val name: String? = null)

    data class Company(val id: Int? = null,
                       val name: String? = null,
                       val addresses: List<Address>? = null)

    data class Address(val id: Int? = null,
                       val street: String? = null,
                       val number: Int? = null,
                       val city: City? = null)

    data class City(val id: Int? = null,
                    val name: String? = null,
                    val state: State? = null)

    data class State(val id: Int? = null,
                     val name: String? = null,
                     val country: Country? = null)

    data class Country(val id: Int? = null,
                       val name: String? = null)
}

class DefaultConstraintsValidatorTest {

    @Test
    fun `isNull with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isNull()
        })
    }

    @Test
    fun `isNull with not null property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isNull()
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = Null()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = Null(), message = "Must be null"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = Null(), message = "Must be null"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = Null(), message = "Deve ser nulo"))))
    }

    @Test
    fun `isNotNull with not null property should be valid`() {
        validate(Employee(id = 1), {
            validate(Employee::id).isNotNull()
        })
    }

    @Test
    fun `isNotNull with null property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(), {
                validate(Employee::id).isNotNull()
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", constraint = NotNull()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", constraint = NotNull(), message = "Não deve ser nulo"))))
    }

    @Test
    fun `isEqualTo with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isEqualTo(1)
        })
    }

    @Test
    fun `isEqualTo with same value should be valid`() {
        validate(Employee(id = 1), {
            validate(Employee::id).isEqualTo(1)
        })
    }

    @Test
    fun `isEqualTo with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 2), {
                validate(Employee::id).isEqualTo(1)
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 2, constraint = Equals(1)))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Equals(1), message = "Must be equal to 1"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Equals(1), message = "Must be equal to 1"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Equals(1), message = "Deve ser igual a 1"))))
    }

    @Test
    fun `isNotEqualTo with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isNotEqualTo(1)
        })
    }

    @Test
    fun `isNotEqualTo with different value should be valid`() {
        validate(Employee(id = 2), {
            validate(Employee::id).isNotEqualTo(1)
        })
    }

    @Test
    fun `isNotEqualTo with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isNotEqualTo(1)
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = NotEquals(1)))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotEquals(1), message = "Must not be equal to 1"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotEquals(1), message = "Must not be equal to 1"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotEquals(1), message = "Não deve ser igual a 1"))))
    }

    @Test
    fun `isIn vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isIn(1, 2, 3)
        })
    }

    @Test
    fun `isIn vararg with same value should be valid`() {
        validate(Employee(id = 2), {
            validate(Employee::id).isIn(1, 2, 3)
        })
    }

    @Test
    fun `isIn vararg with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isIn(0, 2, 3)
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = In(setOf(0, 2, 3))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(setOf(0, 2, 3)), message = "Must be in 0, 2, 3"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(setOf(0, 2, 3)), message = "Must be in 0, 2, 3"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(setOf(0, 2, 3)), message = "Deve ser um desses: 0, 2, 3"))))
    }

    @Test
    fun `isIn iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isIn(listOf(1, 2, 3))
        })
    }

    @Test
    fun `isIn iterable with same value should be valid`() {
        validate(Employee(id = 2), {
            validate(Employee::id).isIn(listOf(1, 2, 3))
        })
    }

    @Test
    fun `isIn iterable with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isIn(listOf(0, 2, 3))
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = In(listOf(0, 2, 3))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(listOf(0, 2, 3)), message = "Must be in 0, 2, 3"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(listOf(0, 2, 3)), message = "Must be in 0, 2, 3"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = In(listOf(0, 2, 3)), message = "Deve ser um desses: 0, 2, 3"))))
    }

    @Test
    fun `isNotIn vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isNotIn(0, 2, 3)
        })
    }

    @Test
    fun `isNotIn vararg with different value should be valid`() {
        validate(Employee(id = 1), {
            validate(Employee::id).isNotIn(0, 2, 3)
        })
    }

    @Test
    fun `isNotIn vararg with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isNotIn(1, 2, 3)
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = NotIn(setOf(1, 2, 3))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(setOf(1, 2, 3)), message = "Must not be in 1, 2, 3"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(setOf(1, 2, 3)), message = "Must not be in 1, 2, 3"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(setOf(1, 2, 3)), message = "Não deve ser um desses: 1, 2, 3"))))
    }

    @Test
    fun `isNotIn iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isNotIn(listOf(0, 2, 3))
        })
    }

    @Test
    fun `isNotIn iterable with different value should be valid`() {
        validate(Employee(id = 1), {
            validate(Employee::id).isNotIn(listOf(0, 2, 3))
        })
    }

    @Test
    fun `isNotIn iterable with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 1), {
                validate(Employee::id).isNotIn(listOf(1, 2, 3))
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 1, constraint = NotIn(listOf(1, 2, 3))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(listOf(1, 2, 3)), message = "Must not be in 1, 2, 3"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(listOf(1, 2, 3)), message = "Must not be in 1, 2, 3"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 1, constraint = NotIn(listOf(1, 2, 3)), message = "Não deve ser um desses: 1, 2, 3"))))
    }

    @Test
    fun `isValid with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::id).isValid { it == 1 }
        })
    }

    @Test
    fun `isValid with same value should be valid`() {
        validate(Employee(id = 1), {
            validate(Employee::id).isValid { it == 1 }
        })
    }

    @Test
    fun `isValid with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(id = 2), {
                validate(Employee::id).isValid { it == 1 }
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", value = 2, constraint = Valid<Int?>({ it == 1 })))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Valid<Int?>({ it == 1 }), message = "Must be valid"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Valid<Int?>({ it == 1 }), message = "Must be valid"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "id", value = 2, constraint = Valid<Int?>({ it == 1 }), message = "Deve ser válido"))))
    }

    @Test
    fun `inner null properties should be valid`() {
        validate(Employee(), {
            validate(Employee::company).validate {
                validate(Company::id).isNotNull()
            }
            validate(Employee::address).validate {
                validate(Address::id).isNotNull()
                validate(Address::city).validate {
                    validate(City::id).isNotNull()
                    validate(City::state).validate {
                        validate(State::id).isNotNull()
                        validate(State::country).validate {
                            validate(Country::id).isNotNull()
                        }
                    }
                }
            }
        })
    }

    @Test
    fun `inner not null properties should be valid`() {
        validate(Employee(id = 1, company = Company(id = 1), address =
        Address(id = 1, city = City(id = 1, state =
        State(id = 1, country = Country(id = 1))))), {
            validate(Employee::id).isNotNull()
            validate(Employee::company).validate {
                validate(Company::id).isNotNull()
            }
            validate(Employee::address).validate {
                validate(Address::id).isNotNull()
                validate(Address::city).validate {
                    validate(City::id).isNotNull()
                    validate(City::state).validate {
                        validate(State::id).isNotNull()
                        validate(State::country).validate {
                            validate(Country::id).isNotNull()
                        }
                    }
                }
            }
        })
    }

    @Test
    fun `inner null properties should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(company = Company(), address =
            Address(city = City(state =
            State(country = Country())))), {
                validate(Employee::id).isNotNull()
                validate(Employee::company).validate {
                    validate(Company::id).isNotNull()
                }
                validate(Employee::address).validate {
                    validate(Address::id).isNotNull()
                    validate(Address::city).validate {
                        validate(City::id).isNotNull()
                        validate(City::state).validate {
                            validate(State::id).isNotNull()
                            validate(State::country).validate {
                                validate(Country::id).isNotNull()
                            }
                        }
                    }
                }
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "address.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "address.city.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "address.city.state.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "address.city.state.country.id", constraint = NotNull()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.state.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.state.country.id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.state.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "address.city.state.country.id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "address.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "address.city.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "address.city.state.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "address.city.state.country.id", constraint = NotNull(), message = "Não deve ser nulo"))))
    }

    @Test
    fun `inner null iterable properties should be valid`() {
        validate(Employee(company = Company()), {
            validate(Employee::company).validate {
                validate(Company::addresses).validateForEach {
                    validate(Address::id).isNotNull()
                    validate(Address::city).validate {
                        validate(City::id).isNotNull()
                    }
                }
            }
        })
    }

    @Test
    fun `inner iterable properties should be valid`() {
        validate(Employee(company = Company(addresses = listOf(
                Address(id = 1, city = City(id = 1)),
                Address(id = 1, city = City(id = 1)),
                Address(id = 1, city = City(id = 1))))), {
            validate(Employee::company).validate {
                validate(Company::addresses).validateForEach {
                    validate(Address::id).isNotNull()
                    validate(Address::city).validate {
                        validate(City::id).isNotNull()
                    }
                }
            }
        })
    }

    @Test
    fun `inner iterable properties should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(company = Company(addresses = listOf(
                    Address(city = City()),
                    Address(city = City()),
                    Address(city = City())))), {
                validate(Employee::company).validate {
                    validate(Company::addresses).validateForEach {
                        validate(Address::id).isNotNull()
                        validate(Address::city).validate {
                            validate(City::id).isNotNull()
                        }
                    }
                }
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "company.addresses[0].id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.addresses[0].city.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.addresses[1].id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.addresses[1].city.id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.addresses[2].id", constraint = NotNull()),
                DefaultConstraintViolation(property = "company.addresses[2].city.id", constraint = NotNull()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "company.addresses[0].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[0].city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].city.id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "company.addresses[0].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[0].city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].city.id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].city.id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "company.addresses[0].id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.addresses[0].city.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.addresses[1].city.id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "company.addresses[2].city.id", constraint = NotNull(), message = "Não deve ser nulo"))))
    }

    @Test
    fun `inner null array properties should be valid`() {
        validate(Employee(), {
            validate(Employee::dependents).validateForEach {
                validate(Dependent::id).isNotNull()
            }
        })
    }

    @Test
    fun `inner array properties should be valid`() {
        validate(Employee(dependents = arrayOf(
                Dependent(id = 1),
                Dependent(id = 1),
                Dependent(id = 1))), {
            validate(Employee::dependents).validateForEach {
                validate(Dependent::id).isNotNull()
            }
        })
    }

    @Test
    fun `inner array properties should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(dependents = arrayOf(
                    Dependent(),
                    Dependent(),
                    Dependent())), {
                validate(Employee::dependents).validateForEach {
                    validate(Dependent::id).isNotNull()
                }
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "dependents[0].id", constraint = NotNull()),
                DefaultConstraintViolation(property = "dependents[1].id", constraint = NotNull()),
                DefaultConstraintViolation(property = "dependents[2].id", constraint = NotNull()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "dependents[0].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "dependents[1].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "dependents[2].id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "dependents[0].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "dependents[1].id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "dependents[2].id", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "dependents[0].id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "dependents[1].id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "dependents[2].id", constraint = NotNull(), message = "Não deve ser nulo"))))
    }

    @Test
    fun `should not repeat the property`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(), {
                validate(Employee::id).isNotNull().isEqualTo(1).isIn(1, 2, 3)
                validate(Employee::name).isNotNull().isEqualTo("test").isIn("test1", "test2", "test3")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "id", constraint = NotNull()),
                DefaultConstraintViolation(property = "name", constraint = NotNull()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "name", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Must not be null"),
                        DefaultI18nConstraintViolation(property = "name", constraint = NotNull(), message = "Must not be null"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "id", constraint = NotNull(), message = "Não deve ser nulo"),
                        DefaultI18nConstraintViolation(property = "name", constraint = NotNull(), message = "Não deve ser nulo"))))
    }
}

class TextConstraintsValidatorTest {

    @Test
    fun `isEmpty with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isEmpty()
        })
    }

    @Test
    fun `isEmpty with empty property should be valid`() {
        validate(Employee(name = ""), {
            validate(Employee::name).isEmpty()
        })
    }

    @Test
    fun `isEmpty with blank property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = " "), {
                validate(Employee::name).isEmpty()
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = " ", constraint = Empty()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = " ", constraint = Empty(), message = "Must be empty"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = " ", constraint = Empty(), message = "Must be empty"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = " ", constraint = Empty(), message = "Deve ser vazio"))))
    }

    @Test
    fun `isNotEmpty with blank property should be valid`() {
        validate(Employee(name = " "), {
            validate(Employee::name).isNotEmpty()
        })
    }

    @Test
    fun `isNotEmpty with null or empty property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(email = ""), {
                validate(Employee::name).isNotEmpty()
                validate(Employee::email).isNotEmpty()
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", constraint = NotEmpty()),
                DefaultConstraintViolation(property = "email", value = "", constraint = NotEmpty()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotEmpty(), message = "Must not be empty"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotEmpty(), message = "Must not be empty"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotEmpty(), message = "Must not be empty"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotEmpty(), message = "Must not be empty"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotEmpty(), message = "Não deve ser vazio"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotEmpty(), message = "Não deve ser vazio"))))
    }

    @Test
    fun `isBlank with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isBlank()
        })
    }

    @Test
    fun `isBlank with empty property should be valid`() {
        validate(Employee(name = ""), {
            validate(Employee::name).isBlank()
        })
    }

    @Test
    fun `isBlank with blank property should be valid`() {
        validate(Employee(name = " "), {
            validate(Employee::name).isBlank()
        })
    }

    @Test
    fun `isBlank with not blank property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "a"), {
                validate(Employee::name).isBlank()
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "a", constraint = Blank()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Blank(), message = "Must be blank"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Blank(), message = "Must be blank"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Blank(), message = "Deve estar em branco"))))
    }

    @Test
    fun `isNotBlank with not blank property should be valid`() {
        validate(Employee(name = "a"), {
            validate(Employee::name).isNotBlank()
        })
    }

    @Test
    fun `isNotBlank with null or empty or blank property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(email = "", username = " "), {
                validate(Employee::name).isNotBlank()
                validate(Employee::email).isNotBlank()
                validate(Employee::username).isNotBlank()
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", constraint = NotBlank()),
                DefaultConstraintViolation(property = "email", value = "", constraint = NotBlank()),
                DefaultConstraintViolation(property = "username", value = " ", constraint = NotBlank()))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotBlank(), message = "Must not be blank"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotBlank(), message = "Must not be blank"),
                        DefaultI18nConstraintViolation(property = "username", value = " ", constraint = NotBlank(), message = "Must not be blank"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotBlank(), message = "Must not be blank"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotBlank(), message = "Must not be blank"),
                        DefaultI18nConstraintViolation(property = "username", value = " ", constraint = NotBlank(), message = "Must not be blank"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", constraint = NotBlank(), message = "Não deve estar em branco"),
                        DefaultI18nConstraintViolation(property = "email", value = "", constraint = NotBlank(), message = "Não deve estar em branco"),
                        DefaultI18nConstraintViolation(property = "username", value = " ", constraint = NotBlank(), message = "Não deve estar em branco"))))
    }

    @Test
    fun `isEqualToIgnoringCase with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isEqualToIgnoringCase("a")
        })
    }

    @Test
    fun `isEqualToIgnoringCase with same value should be valid`() {
        validate(Employee(name = "A"), {
            validate(Employee::name).isEqualToIgnoringCase("a")
        })
    }

    @Test
    fun `isEqualToIgnoringCase with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "a"), {
                validate(Employee::name).isEqualToIgnoringCase("b")
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "a", constraint = Equals("b")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Equals("b"), message = "Must be equal to b"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Equals("b"), message = "Must be equal to b"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = Equals("b"), message = "Deve ser igual a b"))))
    }

    @Test
    fun `isNotEqualToIgnoringCase with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isNotEqualToIgnoringCase("a")
        })
    }

    @Test
    fun `isNotEqualToIgnoringCase with different value should be valid`() {
        validate(Employee(name = "a"), {
            validate(Employee::name).isNotEqualToIgnoringCase("b")
        })
    }

    @Test
    fun `isNotEqualToIgnoringCase with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "a"), {
                validate(Employee::name).isNotEqualToIgnoringCase("A")
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "a", constraint = NotEquals("A")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = NotEquals("A"), message = "Must not be equal to A"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = NotEquals("A"), message = "Must not be equal to A"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = NotEquals("A"), message = "Não deve ser igual a A"))))
    }

    @Test
    fun `isInIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isInIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `isInIgnoringCase vararg with same value should be valid`() {
        validate(Employee(name = "A"), {
            validate(Employee::name).isInIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `isInIgnoringCase vararg with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "a"), {
                validate(Employee::name).isInIgnoringCase("b", "c")
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "a", constraint = In(setOf("b", "c"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(setOf("b", "c")), message = "Must be in b, c"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(setOf("b", "c")), message = "Must be in b, c"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(setOf("b", "c")), message = "Deve ser um desses: b, c"))))
    }

    @Test
    fun `isInIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isInIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `isInIgnoringCase iterable with same value should be valid`() {
        validate(Employee(name = "A"), {
            validate(Employee::name).isInIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `isInIgnoringCase iterable with different value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "a"), {
                validate(Employee::name).isInIgnoringCase(listOf("b", "c"))
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "a", constraint = In(listOf("b", "c"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(listOf("b", "c")), message = "Must be in b, c"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(listOf("b", "c")), message = "Must be in b, c"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "a", constraint = In(listOf("b", "c")), message = "Deve ser um desses: b, c"))))
    }

    @Test
    fun `isNotInIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isNotInIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `isNotInIgnoringCase vararg with different value should be valid`() {
        validate(Employee(name = "a"), {
            validate(Employee::name).isNotInIgnoringCase("b", "c")
        })
    }

    @Test
    fun `isNotInIgnoringCase vararg with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "A"), {
                validate(Employee::name).isNotInIgnoringCase("a", "b", "c")
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "A", constraint = NotIn(setOf("a", "b", "c"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(setOf("a", "b", "c")), message = "Must not be in a, b, c"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(setOf("a", "b", "c")), message = "Must not be in a, b, c"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(setOf("a", "b", "c")), message = "Não deve ser um desses: a, b, c"))))
    }

    @Test
    fun `isNotInIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).isNotInIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `isNotInIgnoringCase iterable with different value should be valid`() {
        validate(Employee(name = "a"), {
            validate(Employee::name).isNotInIgnoringCase(listOf("b", "c"))
        })
    }

    @Test
    fun `isNotInIgnoringCase iterable with same value should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "A"), {
                validate(Employee::name).isNotInIgnoringCase(listOf("a", "b", "c"))
            })
        }
        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "A", constraint = NotIn(listOf("a", "b", "c"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(listOf("a", "b", "c")), message = "Must not be in a, b, c"))),
                entry(Locales.EN, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(listOf("a", "b", "c")), message = "Must not be in a, b, c"))),
                entry(Locales.PT_BR, setOf(DefaultI18nConstraintViolation(
                        property = "name", value = "A", constraint = NotIn(listOf("a", "b", "c")), message = "Não deve ser um desses: a, b, c"))))
    }

    @Test
    fun `size with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).hasSize(min = 1, max = 10)
        })
    }

    @Test
    fun `size with valid min length property should be valid`() {
        validate(Employee(name = "John"), {
            validate(Employee::name).hasSize(min = 4)
        })
    }

    @Test
    fun `size with valid max length property should be valid`() {
        validate(Employee(name = "John"), {
            validate(Employee::name).hasSize(max = 4)
        })
    }

    @Test
    fun `size with valid min and max length property should be valid`() {
        validate(Employee(name = "John"), {
            validate(Employee::name).hasSize(min = 4, max = 4)
        })
    }

    @Test
    fun `size without min and max should be valid`() {
        validate(Employee(name = "John"), {
            validate(Employee::name).hasSize()
        })
    }

    @Test
    fun `size with invalid min and max length property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John", email = "john@company.com", username = "john"), {
                validate(Employee::name).hasSize(min = 5)
                validate(Employee::email).hasSize(max = 15)
                validate(Employee::username).hasSize(min = 5, max = 3)
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = Size(min = 5)),
                DefaultConstraintViolation(property = "email", value = "john@company.com", constraint = Size(max = 15)),
                DefaultConstraintViolation(property = "username", value = "john", constraint = Size(min = 5, max = 3)))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Size(min = 5), message = "Size must be greater than or equal to 5"),
                        DefaultI18nConstraintViolation(property = "email", value = "john@company.com", constraint = Size(max = 15), message = "Size must be less than or equal to 15"),
                        DefaultI18nConstraintViolation(property = "username", value = "john", constraint = Size(min = 5, max = 3), message = "Size must be between 5 and 3"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Size(min = 5), message = "Size must be greater than or equal to 5"),
                        DefaultI18nConstraintViolation(property = "email", value = "john@company.com", constraint = Size(max = 15), message = "Size must be less than or equal to 15"),
                        DefaultI18nConstraintViolation(property = "username", value = "john", constraint = Size(min = 5, max = 3), message = "Size must be between 5 and 3"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Size(min = 5), message = "O tamanho deve ser maior ou igual a 5"),
                        DefaultI18nConstraintViolation(property = "email", value = "john@company.com", constraint = Size(max = 15), message = "O tamanho deve ser menor ou igual a 15"),
                        DefaultI18nConstraintViolation(property = "username", value = "john", constraint = Size(min = 5, max = 3), message = "O tamanho deve estar entre 5 e 3"))))
    }

    @Test
    fun `contains with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).contains("a")
        })
    }

    @Test
    fun `contains with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).contains("a")
        })
    }

    @Test
    fun `contains with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).contains("j")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = Contains("j")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("j"), message = "Must contain j"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("j"), message = "Must contain j"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("j"), message = "Deve conter j"))))
    }

    @Test
    fun `containsIgnoringCase with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsIgnoringCase("a")
        })
    }

    @Test
    fun `containsIgnoringCase with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).containsIgnoringCase("a")
        })
    }

    @Test
    fun `containsIgnoringCase with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsIgnoringCase("g")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = Contains("g")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("g"), message = "Must contain g"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("g"), message = "Must contain g"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = Contains("g"), message = "Deve conter g"))))
    }

    @Test
    fun `containsAll vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAll("a", "b", "c")
        })
    }

    @Test
    fun `containsAll vararg with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).containsAll("a", "b", "c")
        })
    }

    @Test
    fun `containsAll vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAll("j", "o", "h", "n")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n")), message = "Must contain j, o, h, n"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n")), message = "Must contain j, o, h, n"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n")), message = "Deve conter j, o, h, n"))))
    }

    @Test
    fun `containsAll iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAll(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAll iterable with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).containsAll(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAll iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAll(listOf("j", "o", "h", "n"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n")), message = "Must contain j, o, h, n"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n")), message = "Must contain j, o, h, n"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n")), message = "Deve conter j, o, h, n"))))
    }

    @Test
    fun `containsAllIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAllIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `containsAllIgnoringCase vararg with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).containsAllIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `containsAllIgnoringCase vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAllIgnoringCase("j", "o", "h", "n", "k")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n", "k"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n", "k")), message = "Must contain j, o, h, n, k"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n", "k")), message = "Must contain j, o, h, n, k"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(setOf("j", "o", "h", "n", "k")), message = "Deve conter j, o, h, n, k"))))
    }

    @Test
    fun `containsAllIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAllIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAllIgnoringCase iterable with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).containsAllIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAllIgnoringCase iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAllIgnoringCase(listOf("j", "o", "h", "n", "k"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n", "k"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n", "k")), message = "Must contain j, o, h, n, k"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n", "k")), message = "Must contain j, o, h, n, k"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAll(listOf("j", "o", "h", "n", "k")), message = "Deve conter j, o, h, n, k"))))
    }

    @Test
    fun `containsAny vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAny("a", "b", "c")
        })
    }

    @Test
    fun `containsAny vararg with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).containsAny("a", "e", "f")
        })
    }

    @Test
    fun `containsAny vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAny("j", "w", "x", "e")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("j", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("j", "w", "x", "e")), message = "Must contain j, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("j", "w", "x", "e")), message = "Must contain j, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("j", "w", "x", "e")), message = "Deve conter j, w, x, e"))))
    }

    @Test
    fun `containsAny iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAny(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAny iterable with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).containsAny(listOf("a", "e", "f"))
        })
    }

    @Test
    fun `containsAny iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAny(listOf("j", "w", "x", "e"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("j", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("j", "w", "x", "e")), message = "Must contain j, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("j", "w", "x", "e")), message = "Must contain j, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("j", "w", "x", "e")), message = "Deve conter j, w, x, e"))))
    }

    @Test
    fun `containsAnyIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAnyIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `containsAnyIgnoringCase vararg with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).containsAnyIgnoringCase("a", "e", "f")
        })
    }

    @Test
    fun `containsAnyIgnoringCase vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAnyIgnoringCase("w", "x", "e")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("w", "x", "e")), message = "Must contain w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("w", "x", "e")), message = "Must contain w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(setOf("w", "x", "e")), message = "Deve conter w, x, e"))))
    }

    @Test
    fun `containsAnyIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).containsAnyIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `containsAnyIgnoringCase iterable with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).containsAnyIgnoringCase(listOf("a", "e", "f"))
        })
    }

    @Test
    fun `containsAnyIgnoringCase iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).containsAnyIgnoringCase(listOf("w", "x", "e"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("w", "x", "e")), message = "Must contain w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("w", "x", "e")), message = "Must contain w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = ContainsAny(listOf("w", "x", "e")), message = "Deve conter w, x, e"))))
    }

    @Test
    fun `doesNotContain with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContain("a")
        })
    }

    @Test
    fun `doesNotContain with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContain("d")
        })
    }

    @Test
    fun `doesNotContain with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContain("J")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContain("J")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("J"), message = "Must not contain J"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("J"), message = "Must not contain J"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("J"), message = "Não deve conter J"))))
    }

    @Test
    fun `doesNotContainIgnoringCase with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainIgnoringCase("a")
        })
    }

    @Test
    fun `doesNotContainIgnoringCase with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContainIgnoringCase("d")
        })
    }

    @Test
    fun `doesNotContainIgnoringCase with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainIgnoringCase("j")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContain("j")))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("j"), message = "Must not contain j"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("j"), message = "Must not contain j"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContain("j"), message = "Não deve conter j"))))
    }

    @Test
    fun `doesNotContainAll vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAll("a", "b", "c")
        })
    }

    @Test
    fun `doesNotContainAll vararg with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContainAll("a", "b", "c", "d")
        })
    }

    @Test
    fun `doesNotContainAll vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAll("J", "o", "h", "n")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "o", "h", "n"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "o", "h", "n")), message = "Must not contain J, o, h, n"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "o", "h", "n")), message = "Must not contain J, o, h, n"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "o", "h", "n")), message = "Não deve conter J, o, h, n"))))
    }

    @Test
    fun `doesNotContainAll iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAll(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `doesNotContainAll iterable with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContainAll(listOf("a", "b", "c", "d"))
        })
    }

    @Test
    fun `doesNotContainAll iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAll(listOf("J", "o", "h", "n"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "o", "h", "n"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "o", "h", "n")), message = "Must not contain J, o, h, n"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "o", "h", "n")), message = "Must not contain J, o, h, n"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "o", "h", "n")), message = "Não deve conter J, o, h, n"))))
    }

    @Test
    fun `doesNotContainAllIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAllIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `doesNotContainAllIgnoringCase vararg with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).doesNotContainAllIgnoringCase("a", "b", "c", "d")
        })
    }

    @Test
    fun `doesNotContainAllIgnoringCase vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAllIgnoringCase("J", "O", "H", "N")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "O", "H", "N"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "O", "H", "N")), message = "Must not contain J, O, H, N"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "O", "H", "N")), message = "Must not contain J, O, H, N"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(setOf("J", "O", "H", "N")), message = "Não deve conter J, O, H, N"))))
    }

    @Test
    fun `doesNotContainAllIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAllIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `doesNotContainAllIgnoringCase iterable with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).doesNotContainAllIgnoringCase(listOf("a", "b", "c", "d"))
        })
    }

    @Test
    fun `doesNotContainAllIgnoringCase iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAllIgnoringCase(listOf("J", "O", "H", "N"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "O", "H", "N"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "O", "H", "N")), message = "Must not contain J, O, H, N"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "O", "H", "N")), message = "Must not contain J, O, H, N"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAll(listOf("J", "O", "H", "N")), message = "Não deve conter J, O, H, N"))))
    }

    @Test
    fun `doesNotContainAny vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAny("a", "b", "c")
        })
    }

    @Test
    fun `doesNotContainAny vararg with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContainAny("e", "f")
        })
    }

    @Test
    fun `doesNotContainAny vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAny("J", "w", "x", "e")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("J", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("J", "w", "x", "e")), message = "Must not contain J, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("J", "w", "x", "e")), message = "Must not contain J, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("J", "w", "x", "e")), message = "Não deve conter J, w, x, e"))))
    }

    @Test
    fun `doesNotContainAny iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAny(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `doesNotContainAny iterable with valid property should be valid`() {
        validate(Employee(name = "abc"), {
            validate(Employee::name).doesNotContainAny(listOf("e", "f"))
        })
    }

    @Test
    fun `doesNotContainAny iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAny(listOf("J", "w", "x", "e"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("J", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("J", "w", "x", "e")), message = "Must not contain J, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("J", "w", "x", "e")), message = "Must not contain J, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("J", "w", "x", "e")), message = "Não deve conter J, w, x, e"))))
    }

    @Test
    fun `doesNotContainAnyIgnoringCase vararg with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAnyIgnoringCase("a", "b", "c")
        })
    }

    @Test
    fun `doesNotContainAnyIgnoringCase vararg with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).doesNotContainAnyIgnoringCase("e", "f")
        })
    }

    @Test
    fun `doesNotContainAnyIgnoringCase vararg with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAnyIgnoringCase("j", "w", "x", "e")
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("j", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("j", "w", "x", "e")), message = "Must not contain j, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("j", "w", "x", "e")), message = "Must not contain j, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(setOf("j", "w", "x", "e")), message = "Não deve conter j, w, x, e"))))
    }

    @Test
    fun `doesNotContainAnyIgnoringCase iterable with null property should be valid`() {
        validate(Employee(), {
            validate(Employee::name).doesNotContainAnyIgnoringCase(listOf("a", "b", "c"))
        })
    }

    @Test
    fun `doesNotContainAnyIgnoringCase iterable with valid property should be valid`() {
        validate(Employee(name = "ABC"), {
            validate(Employee::name).doesNotContainAnyIgnoringCase(listOf("e", "f"))
        })
    }

    @Test
    fun `doesNotContainAnyIgnoringCase iterable with invalid property should be invalid`() {
        val exception = assertThrows<ConstraintViolationException> {
            validate(Employee(name = "John"), {
                validate(Employee::name).doesNotContainAnyIgnoringCase(listOf("j", "w", "x", "e"))
            })
        }

        assertThat(exception.constraintViolations).containsExactly(
                DefaultConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("j", "w", "x", "e"))))

        val i18nMap: Map<Locale, Set<I18nConstraintViolation>> = ValidatorFixture.supportedLocales
                .map { it to exception.constraintViolations.mapToI18n(it) }.toMap()

        assertThat(i18nMap).containsExactly(
                entry(Locales.DEFAULT, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("j", "w", "x", "e")), message = "Must not contain j, w, x, e"))),
                entry(Locales.EN, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("j", "w", "x", "e")), message = "Must not contain j, w, x, e"))),
                entry(Locales.PT_BR, setOf(
                        DefaultI18nConstraintViolation(property = "name", value = "John", constraint = NotContainAny(listOf("j", "w", "x", "e")), message = "Não deve conter j, w, x, e"))))
    }
}