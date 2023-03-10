# Virtual MR Scanner

A realistic simulation of a MRI was developed. For the user it should be possible to change all relevant setting of the virtual scanner and to adapt them to the expected pathology. Students and doctors in training are the target group.
At the moment the pulse sequence classes SR, IR, SE, TSE, FLASH and FISP are implemented. By a plugin mechanism this list can be easily extended. Parameters, like TR, TE, TI, flip-angle or echo train length, can be adjusted. The choice of matrix size, FOV, slice-thickness and number of acquisitions affect the signal-to-noise ratio of the images. In a first step, the simulation calculates the signal intensity in the k-space. Aliasing- and motion-artifacts are simulated by modifying the k-space data. In a last step, a 2D-fourier-transform of the k-space data is performed. Window and center of the resulting images can be changed. The simulation time can be optionally delayed till the real measuring time. The algorithms of the simulation are based on parameter-images of the three physical basic items T1, T2 and proton density. They are calculated once from a patient examination with suitable pulse sequences. To work out pathologies a small graphic tool is included. The graphical user interface is oriented on a real MR scanner. The software is implemented in pure Java under the GPL license.

On a 500 MHz PC the software calculates an image in 5 to 20 seconds, depending on the pulse sequence and the degree of the desired artifacts.

An interactive simulation of a real world examination is possible on a standard PC. The users can study the operation of a costly and not everywhere available equipment on their desktop.

## RSNA 2002
The current version of the simulation was presented at the RSNA 2002, 88th Scientific Assembly and Annual Meeting, Chicago, IL, USA. During the meeting a lecture was given. The presentation was given the RSNA infoRAD award cum laude.

## Usage

* Download the complate package as a ZIP file.
* Decompress the file locally.
	* For Windows: Run one of the batchfiles `vmrt_xx.bat`. Use your preferred language, e.g. `vmrt_en.bat` for English.
	* For MacOS: Doubleclick the application `vmrt_mac.app`.

## References

Schalla C, Tr??mper A: Entwicklung eines virtuellen klinischen Kernspintomographen. Diplomarbeit 1999. Interner Bericht der Universit??t Dortmund, Fachbereich Informatik.

Hackl??nder T, Schalla C, Tr??mper A, Mertens H, Hiltner J: Virtueller Kernspintomograph. 81. Deutscher R??ntgenkongre??, Wiesbaden. RoFo Fortschr Geb R??ntgenstr Neuen Bildgeb Verfahr 172 (2000) S135.

Hacklaender T, Schalla C, Truemper A, Mertens H, Hiltner J, Cramer BM: A Virtual MR Scanner for Education. RSNA 2002, 88th Scientific Assembly and Annual Meeting, Chicago, USA. Radiology 225 (P) (2002) 757.

Hackl??nder T, Mertens H, Cramer BM: Computersimulation eines klinischen Magnetresonanztomographen f??r Ausbildungszwecke. RoFo Fortschr Geb R??ntgenstr Neuen Bildgeb Verfahr 176 (2004) 1151-1156

Hackl??nder T, Mertens H: Virtual MRI: A PC based simulation of a clinical MR scanner. Acad Radiol 12,1 (2005) 85-96