import 'package:flutter/material.dart';

/// A combined slider + text field for numeric parameter input.
class ParamSlider extends StatelessWidget {
  final String label;
  final double value;
  final double min;
  final double max;
  final int decimals;
  final String unit;
  final ValueChanged<double> onChanged;

  const ParamSlider({
    super.key,
    required this.label,
    required this.value,
    required this.min,
    required this.max,
    required this.onChanged,
    this.decimals = 1,
    this.unit = '',
  });

  @override
  Widget build(BuildContext context) {
    final suffix = unit.isNotEmpty ? ' $unit' : '';
    return Row(
      children: [
        SizedBox(
          width: 100,
          child: Text('$label$suffix:',
              style: Theme.of(context).textTheme.bodySmall),
        ),
        Expanded(
          child: Slider(
            value: value.clamp(min, max),
            min: min,
            max: max,
            onChanged: onChanged,
          ),
        ),
        SizedBox(
          width: 70,
          child: TextFormField(
            key: ValueKey('${label}_${value.toStringAsFixed(decimals)}'),
            initialValue: value.toStringAsFixed(decimals),
            keyboardType: const TextInputType.numberWithOptions(decimal: true),
            style: Theme.of(context).textTheme.bodySmall,
            decoration: const InputDecoration(
              isDense: true,
              contentPadding: EdgeInsets.symmetric(horizontal: 6, vertical: 6),
            ),
            onFieldSubmitted: (text) {
              final v = double.tryParse(text);
              if (v != null) {
                onChanged(v.clamp(min, max));
              }
            },
          ),
        ),
      ],
    );
  }
}
