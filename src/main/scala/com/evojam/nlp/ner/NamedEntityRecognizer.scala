package com.evojam.nlp.ner

import epic.sequences.SemiCRF

trait NamedEntityRecognizer {
  def tokenize[L](sentence: String)(crf: SemiCRF[L, String]): IndexedSeq[(L, String)]
}
