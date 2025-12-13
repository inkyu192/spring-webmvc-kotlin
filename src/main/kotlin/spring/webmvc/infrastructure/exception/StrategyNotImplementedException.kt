package spring.webmvc.infrastructure.exception

import org.springframework.http.HttpStatus
import spring.webmvc.domain.model.enums.Category
import kotlin.reflect.KClass

class StrategyNotImplementedException(kClass: KClass<*>, category: Category) :
    AbstractHttpException(
        message = "$category 전략이 ${kClass.simpleName}에 아직 구현되지 않았습니다.",
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    )