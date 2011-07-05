
SRC := $(wildcard *.pic)
OUTPNG := $(SRC:.pic=.png)
OUTPDF := $(SRC:.pic=.pdf)


all: ${OUTPDF} ${OUTPNG}
clean: 
	rm ${OUTPDF} ${OUTPNG}

%.png: %.pdf
	convert -trim $< $@
%.pdf: %.pic
	pic2plot -T ps $< |ps2pdf - $@

.PRECIOUS: *.pdf
