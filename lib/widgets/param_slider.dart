import 'package:flutter/material.dart';

/// A labeled slider with text field for MRI parameter input.
/// Matches SliderPanel from the Java version.
class ParamSlider extends StatefulWidget {
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
  State<ParamSlider> createState() => _ParamSliderState();
}

class _ParamSliderState extends State<ParamSlider> {
  late TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.value.toString());
  }

  @override
  void didUpdateWidget(ParamSlider oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.value != widget.value) {
      _controller.text = widget.value.toString();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        children: [
          SizedBox(
            width: 56,
            child: Text(widget.label,
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
                min: widget.min.toDouble(),
                max: widget.max.toDouble(),
                value: widget.value
                    .clamp(widget.min, widget.max)
                    .toDouble(),
                onChanged: (v) {
                  final intVal = v.round();
                  widget.onChanged(intVal);
                },
              ),
            ),
          ),
          SizedBox(
            width: 50,
            child: TextField(
              controller: _controller,
              keyboardType: TextInputType.number,
              style: const TextStyle(fontSize: 11),
              decoration: const InputDecoration(
                isDense: true,
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 4, vertical: 4),
                border: OutlineInputBorder(),
              ),
              onSubmitted: (text) {
                final val = int.tryParse(text);
                if (val != null) {
                  widget.onChanged(val.clamp(widget.min, widget.max));
                }
              },
            ),
          ),
          if (widget.unit.isNotEmpty)
            Padding(
              padding: const EdgeInsets.only(left: 2),
              child: Text(widget.unit, style: const TextStyle(fontSize: 10)),
            ),
        ],
      ),
    );
  }
}
