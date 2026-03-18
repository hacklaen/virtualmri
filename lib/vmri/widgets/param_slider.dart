import 'package:flutter/material.dart';

/// A labeled slider with text field for MRI parameter input.
/// Matches SliderPanel from the Java version.
class ParamSlider extends StatelessWidget {
  final String label;
  final String unit;
  final int min;
  final int max;
  final int value;
  final ValueChanged<int> onChanged;
  final int? step;

  const ParamSlider({
    super.key,
    required this.label,
    required this.unit,
    required this.min,
    required this.max,
    required this.value,
    required this.onChanged,
    this.step,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          SizedBox(
            width: 56,
            child: Text(label,
                style: const TextStyle(fontSize: 11),
                overflow: TextOverflow.ellipsis),
          ),
          Expanded(
            child: SliderTheme(
              data: SliderTheme.of(context).copyWith(
                trackHeight: 2,
                thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 6),
              ),
              child: Slider(
                min: min.toDouble(),
                max: max.toDouble(),
                value: value
                    .clamp(min, max)
                    .toDouble(),
                onChanged: (v) {
                  final intVal = v.round();
                  onChanged(intVal);
                },
              ),
            ),
          ),
          SizedBox(
            width: 50,
            child: TextFormField(
              key: ValueKey('${label}_$value'),
              initialValue: value.toString(),
              keyboardType: TextInputType.number,
              style: const TextStyle(fontSize: 11),
              decoration: const InputDecoration(
                isDense: true,
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 4, vertical: 4),
                border: OutlineInputBorder(),
              ),
              onFieldSubmitted: (text) {
                final val = int.tryParse(text);
                if (val != null) {
                  onChanged(val.clamp(min, max));
                }
              },
            ),
          ),
          if (unit.isNotEmpty)
            Padding(
              padding: const EdgeInsets.only(left: 2),
              child: Text(unit, style: const TextStyle(fontSize: 10)),
            ),
        ],
      ),
    );
  }
}
