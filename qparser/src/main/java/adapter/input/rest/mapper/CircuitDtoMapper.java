package adapter.input.rest.mapper;

import adapter.input.rest.dto.CircuitLayerDto;
import adapter.input.rest.dto.CircuitResponseDto;
import adapter.input.rest.dto.GateDto;
import adapter.input.rest.dto.MeasurementDto;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;

import java.util.List;
import java.util.stream.Collectors;

public class CircuitDtoMapper {

    public CircuitResponseDto toDto(Circuit circuit) {
        if (circuit == null) return null;
        List<CircuitLayerDto> layerDtos = circuit.layers().stream()
                .map(this::toLayerDto)
                .collect(Collectors.toList());
        return new CircuitResponseDto(
                circuit.numberOfQubits(),
                circuit.numberOfClBits(),
                circuit.getAllGates(),
                circuit.getAllMeasurements(),
                layerDtos
        );
    }

    private CircuitLayerDto toLayerDto(CircuitLayer layer) {
        List<GateDto> gateDtos = layer.getGates().stream()
                .map(this::toGateDto)
                .collect(Collectors.toList());

        List<MeasurementDto> measurementDtos = layer.getMeasurements().stream()
                .map(this::toMeasurementDto)
                .collect(Collectors.toList());

        return new CircuitLayerDto(
                layer.getNumberOfGates(),
                layer.getNumberOfMeasurements(),
                gateDtos,
                measurementDtos
        );
    }

    private GateDto toGateDto(Gate gate) {
        return new GateDto(
                gate.name(),
                gate.targetQubits(),
                gate.controlQubits(),
                gate.parameters()
        );
    }

    private MeasurementDto toMeasurementDto(Measurement measurement) {
        return new MeasurementDto(
                measurement.qubitIndex(),
                measurement.bitIndex()
        );
    }
}
