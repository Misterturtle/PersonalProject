HotKeySet("^!x", "MyExit")

StartHearthstone()
;SelectTodaysQuests()
;SelectSoloAdventures()
;SelectPractice()
;SelectDeck()
;SelectConfirm()
;SelectCard3of5()
SelectCardOf7(5)
EndTurn()

Func MyExit()
	Exit
EndFunc

Func StartBattleNet()
	If not WinExists("Battle.net") Then
		run("C:\Program Files (x86)\Battle.net\Battle.net.6526\Battle.net.exe")
		WinWait("Battle.net")
	EndIf

	WinActivate("Battle.net")
	WinMove("Battle.net", "", 0,0,1024,768)
EndFunc

Func StartHearthStone()
	If not WinExists("Hearthstone") Then
		StartBattleNet()
		MouseClick("main", 284, 703)
		WinWait("Hearthstone")
	EndIf

	WinMove("Hearthstone", "", 0,0,1884,1080)
EndFunc

Func WaitFor($x, $y, $hex)
	While Hex(PixelGetColor($x,$y), 6) <> $hex
		ToolTip(Hex(PixelGetColor($x,$y), 6))
	WEnd
EndFunc

Func SelectTodaysQuests()
	WaitFor(922,515, "312CE7")
	MouseClick("main", 933,517)
EndFunc

Func SelectSoloAdventures()
	WaitFor(339, 920, "D2AB51")
	MouseClick("main", 921,421)
EndFunc

Func SelectPractice()
	WaitFor(647,237, "AA311B")
	MouseClick("main", 1347, 887)
EndFunc

Func SelectDeck()
	WaitFor(453,399, "FFDF63")
	MouseClick("main", 1362,880)
	WaitFor(1384, 214, "42454B")
	MouseClick("main",1384, 214)
	MouseClick("main",1378,871)
EndFunc

Func SelectConfirm()
	WaitFor(954,242,"FDD129")
	MouseClick("main", 944,860)
EndFunc

Func WaitForEndTurn()
	While 1
		$p = Hex(PixelGetColor(1509, 495), 6)
		If $p = "28CB05" or $p = "EED103" Then
			Return
		EndIf
		ToolTip($p)
	WEnd
EndFunc

Func EndTurn()
	WaitForEndTurn()
	MouseClick("main", 1507, 495)
EndFunc

Func Click($x,$y)
	MouseClick("main", $x,$y)
EndFunc

Func SelectCard5of6()
	WaitForEndTurn()
	MouseClick("main", 1018, 1025)
	MouseClick("main", 963,524)
EndFunc

Func SelectCard3Of5()
	WaitForEndTurn()
	MouseClick("main", 890,1002)
	MouseClick("main", 963,524)
EndFunc

Func SelectCardOf7($slot)
	Click(654 + ($slot - 1) * 76 ,1062)
EndFunc

Func PlaceCardInField()
	Click(963,524)
EndFunc
