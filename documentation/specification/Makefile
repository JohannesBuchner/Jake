pdf: specification.pdf

%.pdf: *.tex software_requirements_spec/*.tex tech_spec/*.tex ui_spec/*.tex
	rm -f $*.pdf
	pdflatex $*.tex
	pdflatex $*.tex # to have the right reference pages, you have to exectue again

clean:
	rm -f *.pdf
	rm -f *.log
	rm -f *.aux
	rm -f *.toc
	rm -f *.out

.PHONY: clean
