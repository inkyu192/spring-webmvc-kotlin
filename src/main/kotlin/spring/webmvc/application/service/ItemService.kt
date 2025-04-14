package spring.webmvc.application.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Item
import spring.webmvc.domain.repository.ItemRepository
import spring.webmvc.presentation.dto.request.ItemSaveRequest
import spring.webmvc.presentation.dto.response.ItemResponse
import spring.webmvc.presentation.exception.EntityNotFoundException

@Service
@Transactional(readOnly = true)
class ItemService(
    private val itemRepository: ItemRepository,
) {
    @Transactional
    fun saveItem(itemSaveRequest: ItemSaveRequest): ItemResponse {
        val item = itemRepository.save(
            Item.create(
                name = itemSaveRequest.name,
                description = itemSaveRequest.description,
                price = itemSaveRequest.price,
                quantity = itemSaveRequest.quantity,
                category = itemSaveRequest.category,
            )
        )

        return ItemResponse(item)
    }

    fun findItems(pageable: Pageable, name: String?) =
        itemRepository.findAll(pageable = pageable, name = name).map { ItemResponse(it) }

    fun findItem(id: Long): ItemResponse {
        val item = itemRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(clazz = Item::class.java, id = id)

        return ItemResponse(item)
    }

    @Transactional
    fun putItem(id: Long, itemSaveRequest: ItemSaveRequest) =
        itemRepository.findByIdOrNull(id)
            ?.let { false to updateItem(item = it, itemSaveRequest = itemSaveRequest) }
            ?: (true to saveItem(itemSaveRequest))

    private fun updateItem(item: Item, itemSaveRequest: ItemSaveRequest): ItemResponse {
        item.update(
            name = itemSaveRequest.name,
            description = itemSaveRequest.description,
            price = itemSaveRequest.price,
            quantity = itemSaveRequest.quantity,
            category = itemSaveRequest.category,
        )

        return ItemResponse(item)
    }

    @Transactional
    fun deleteItem(id: Long) {
        val item = itemRepository.findByIdOrNull(id) ?: throw EntityNotFoundException(clazz = Item::class.java, id = id)

        itemRepository.delete(item)
    }
}
