#!/usr/bin/env python

import sys
import pygtk
pygtk.require('2.0')
import gtk
import gtk.glade

if len(sys.argv) < 3:
	print sys.argv[0], "<gladefile>", "<windowname>", "[<title>]"
	sys.exit(1)
gladefile = sys.argv[1]
window = sys.argv[2]
if len(sys.argv) > 3:
	title = sys.argv[3]
else:
	title = window
class GladeStarter:
	wTree = None
	mainwindow = None
	def __init__(self):
		self.wTree = gtk.glade.XML(gladefile) 
		self.mainwindow = self.wTree.get_widget(window)
		if not (self.mainwindow):
			print "No such window. "
			sys.exit(2)
		self.mainwindow.show_all()
		self.mainwindow.set_title(title)
		self.mainwindow.connect("destroy", self.on_quit)
	
	def on_quit(self, param):
		gtk.main_quit()
	
if __name__ == "__main__":
	gs = GladeStarter()
	gtk.main()

