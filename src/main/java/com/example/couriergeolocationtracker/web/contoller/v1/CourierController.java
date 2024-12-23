package com.example.couriergeolocationtracker.web.contoller.v1;

import com.example.couriergeolocationtracker.service.consumer.CourierService;
import com.example.couriergeolocationtracker.web.model.v1.response.CourierDistanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that provides REST endpoints to query courier information.
 */
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    /**
     * Retrieves the total travel distance of a courier in the requested unit (km or miles).
     *
     * @param courierId ID of the courier
     * @param unit      The requested unit. Defaults to "km" if not specified.
     * @return CourierDistanceResponse containing the distance and the chosen unit
     */
    @Operation(
        summary = "Retrieve courier total travel distance",
        description = "Returns the total travel distance of a courier, converted to the specified unit (km or miles).",
        parameters = {
            @Parameter(
                name = "courierId",
                required = true,
                in = ParameterIn.PATH),
            @Parameter(
                name = "unit",
                description = "Unit for the distance (km or mi)",
                required = true,
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string", allowableValues = {"km", "mi"}, defaultValue = "km"),
                examples = {
                    @ExampleObject(value = "km", description = "Distance in kilometers"),
                    @ExampleObject(value = "mi", description = "Distance in miles")
                })},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved courier distance",
                content = @Content(
                    schema = @Schema(implementation = CourierDistanceResponse.class)
                ))})
    @GetMapping("/{courierId}/distance")
    public CourierDistanceResponse getTotalTravelDistance(
        @PathVariable Long courierId,
        @RequestParam(defaultValue = "km") String unit) {
        return courierService.getTotalTravelDistance(courierId, unit);
    }
}
