package com.evojam.nlp.ner

import com.google.inject.Inject
import epic.sequences.{Segmentation, SemiCRF}

private[nlp] class NamedEntityRecognizerImpl @Inject() () extends NamedEntityRecognizer {
  type UnpackedSegmentation[L] = IndexedSeq[(L, IndexedSeq[String])]

  override def tokenize[L](sentence: String)(crf: SemiCRF[L, String]) = {
    val unpackedSegmentation = unpackSegmentation {
      crf.bestSequence(epic.preprocess.tokenize(sentence))
    }
    flatten(unpackedSegmentation)
  }

  private def unpackSegmentation[L](segmentation: Segmentation[L, String]): UnpackedSegmentation[L] =
    segmentation.segments.map {
      case (label, span) =>
        (label, subsequence(segmentation.words, span.begin, span.end))
    }

  private def flatten[L](unpackedSegmentation: UnpackedSegmentation[L]): IndexedSeq[(L, String)] =
    unpackedSegmentation.map {
      case (label, words) =>
        (label, words.mkString(" "))
    }

  private def subsequence[T](seq: IndexedSeq[T], beg: Int, end: Int): IndexedSeq[T] =
    (beg until end).map(seq)
}
