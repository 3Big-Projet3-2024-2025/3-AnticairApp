package be.anticair.anticairapi.Class;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "antiquity")
public class Listing {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_antiquity")
        private Integer idAntiquity;

        @Column(name = "price_antiquity")
        private Double priceAntiquity;

        @Column(name = "description_antiquity")
        private String descriptionAntiquity;

        @Column(name = "title_antiquity")
        private String titleAntiquity;

        @Column(name = "mail_member")
        private String mailMember;

        @Column(name = "state")
        private Integer state;

        @Column(name = "est_affiche")
        private Boolean estAffiche;

        @Column(name = "id_member")
        private Integer idMember;

        public void applyCommission(){
                this.priceAntiquity += this.priceAntiquity * 0.20;
        }

        // Getters et setters

        public Integer getIdAntiquity() {
                return idAntiquity;
        }

        public void setIdAntiquity(Integer idAntiquity) {
                this.idAntiquity = idAntiquity;
        }

        public Double getPriceAntiquity() {
                return priceAntiquity;
        }

        public void setPriceAntiquity(Double priceAntiquity) {
                this.priceAntiquity = priceAntiquity;
        }

        public String getDescriptionAntiquity() {
                return descriptionAntiquity;
        }

        public void setDescriptionAntiquity(String descriptionAntiquity) {
                this.descriptionAntiquity = descriptionAntiquity;
        }

        public String getTitleAntiquity() {
                return titleAntiquity;
        }

        public void setTitleAntiquity(String titleAntiquity) {
                this.titleAntiquity = titleAntiquity;
        }

        public String getMailMember() {
                return mailMember;
        }

        public void setMailMember(String mailMember) {
                this.mailMember = mailMember;
        }

        public Integer getState() {
                return state;
        }

        public void setState(Integer state) {
                this.state = state;
        }

        public Boolean getEstAffiche() {
                return estAffiche;
        }

        public void setEstAffiche(Boolean estAffiche) {
                this.estAffiche = estAffiche;
        }

        public Integer getIdMember() {
                return idMember;
        }

        public void setIdMember(Integer idMember) {
                this.idMember = idMember;
        }
}
