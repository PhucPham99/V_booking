package com.vbooking.backend.modules.hotel.service.impl;

import com.vbooking.backend.dto.common.PageableResponse;
import com.vbooking.backend.dto.hotel.HotelDTO;
import com.vbooking.backend.dto.hotel.HotelImageDTO;
import com.vbooking.backend.dto.search.HotelSearchRequest;
import com.vbooking.backend.infrastructure.exception.ResourceNotFoundException;
import com.vbooking.backend.modules.hotel.entity.HotelEntity;
import com.vbooking.backend.modules.hotel.repository.HotelRepository;
import com.vbooking.backend.modules.hotel.service.HotelService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vbooking.backend.dto.hotel.RoomTypeDTO;
// ... existing imports

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final com.vbooking.backend.modules.inventory.repository.RoomTypeRepository roomTypeRepository;
    private final com.vbooking.backend.modules.inventory.repository.RoomRateRepository roomRateRepository;

    @Override
    public List<RoomTypeDTO> getHotelRoomTypes(Long hotelId) {
        // Query real room types from database
        List<com.vbooking.backend.modules.inventory.entity.RoomTypeEntity> roomTypes = roomTypeRepository
                .findByHotelIdAndIsActiveTrueAndIsCancelFalse(hotelId);

        if (roomTypes.isEmpty()) {
            return List.of(); // Return empty list if no rooms found
        }

        // Map entities to DTOs
        return roomTypes.stream()
                .map(this::mapRoomTypeToDTO)
                .collect(Collectors.toList());
    }

    private RoomTypeDTO mapRoomTypeToDTO(com.vbooking.backend.modules.inventory.entity.RoomTypeEntity entity) {
        return RoomTypeDTO.builder()
                .id(entity.getRoomTypeId())
                .name(entity.getName())
                .nameEn(entity.getName()) // TODO: Add nameEn field to RoomTypeEntity if needed
                .size(entity.getSizeM2() != null ? entity.getSizeM2().intValue() + " m²" : "N/A")
                .maxAdults(entity.getMaxAdults())
                .maxChildren(entity.getMaxChildren())
                .beds(entity.getBedConfig())
                .pricePerNight(entity.getDefaultPrice()) // Base price from room_type
                .available(entity.getTotalRooms()) // Total rooms available for this type
                .amenities(entity.getAmenities() != null ? entity.getAmenities() : List.of())
                .imageUrl(entity.getImages() != null && !entity.getImages().isEmpty()
                        ? entity.getImages().get(0)
                        : null)
                .build();
    }

    @Override
    public PageableResponse<HotelDTO> searchHotels(HotelSearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;

        // Sort logic
        Sort sort = Sort.by("averageRating").descending(); // Default
        if (StringUtils.hasText(request.getSortBy())) {
            switch (request.getSortBy()) {
                case "price_asc":
                    // Sort by price is complex without joining room types.
                    // For now, we sort by star rating as a proxy or keep default
                    sort = Sort.by("starRating").ascending();
                    break;
                case "price_desc":
                    sort = Sort.by("starRating").descending();
                    break;
                case "rating_desc":
                    sort = Sort.by("averageRating").descending();
                    break;
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<HotelEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Active only
            predicates.add(cb.equal(root.get("status"), "active"));
            predicates.add(cb.equal(root.get("isCancel"), false));

            // Keyword (Name or City)
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), keyword);
                Predicate cityLike = cb.like(cb.lower(root.get("city")), keyword);
                predicates.add(cb.or(nameLike, cityLike));
            }

            // City
            if (StringUtils.hasText(request.getCity())) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + request.getCity().toLowerCase() + "%"));
            }

            // Star Rating
            if (request.getStarRating() != null && !request.getStarRating().isEmpty()) {
                predicates.add(root.get("starRating").in(request.getStarRating()));
            }

            // Note: Price filter requires joining w/ RoomType.
            // Skipping for MVP to avoid complexity, or assuming base price logic later.

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HotelEntity> hotelPage = hotelRepository.findAll(spec, pageable);

        List<HotelDTO> hotelDTOs = hotelPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageableResponse<>(
                hotelDTOs,
                hotelPage.getNumber(),
                hotelPage.getSize(),
                hotelPage.getTotalElements(),
                hotelPage.getTotalPages(),
                hotelPage.isLast(),
                hotelPage.isFirst());
    }

    @Override
    public HotelDTO getHotelDetail(Long id) {
        HotelEntity hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        return mapToDTO(hotel);
    }

    @Override
    public HotelDTO getHotelDetailBySlug(String slug) {
        HotelEntity hotel = hotelRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "slug", slug));
        return mapToDTO(hotel);
    }

    private HotelDTO mapToDTO(HotelEntity entity) {
        return HotelDTO.builder()
                .hotelId(entity.getHotelId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .city(entity.getCity())
                .district(entity.getDistrict())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .starRating(entity.getStarRating())
                .averageRating(entity.getAverageRating())
                .totalReviews(entity.getTotalReviews())
                .status(entity.getStatus())
                .amenities(entity.getAmenities())
                .checkinTime(entity.getCheckinTime())
                .checkoutTime(entity.getCheckoutTime())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                // In real app, fetch images, policies from separate repos or relationships
                .images(List.of())
                .build();
    }
}
