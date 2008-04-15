
%.pdf: *.tex
	rm -f $*.pdf
	pdflatex $*.tex
	pdflatex $*.tex # to have the right reference pages, you have to exectue again

clean:
	rm -f *.pdf

.PHONY: clean
