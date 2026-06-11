package pl.lewica.lewicapl.android.parsing.dto

data class AnnouncementDto(
  val id: Int,
  val title: String,
  val place: String,
  val happeningAt: String,
  val body: String,
  val publishedBy: String,
  val publishedByEmail: String,
  val publishedAt: String?
)
