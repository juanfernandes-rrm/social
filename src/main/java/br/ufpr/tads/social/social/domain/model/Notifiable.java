package br.ufpr.tads.social.social.domain.model;

import java.io.Serializable;
import java.util.UUID;

interface Notifiable extends Serializable {
    UUID getId();
}
