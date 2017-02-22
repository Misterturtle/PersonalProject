package tph

/**
  * Created by Harambe on 2/20/2017.
  */

  object LogFileAction {

    trait LogFileAction {


    }

    case class DISCOVER_OPTION(option: Int) extends LogFileAction

    case class FACE_ATTACK_VALUE(player: Int, value: Int) extends LogFileAction
  }
