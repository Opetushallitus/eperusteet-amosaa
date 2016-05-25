namespace Muodostuminen {
    export const laskeLaajuudet = (rakenne, viitteet) => {
      if (!rakenne) { return; }

      rakenne.$laajuus = rakenne.$laajuus || 0;
      if (rakenne.$laajuusMaksimi > rakenne.$laajuus) {
        rakenne.$laajuus = rakenne.$laajuusMaksimi;
      }

      rakenne.$vaadittuLaajuus = rakenne.$vaadittuLaajuus || 0;

      _.forEach(rakenne.osat, function(osa) {
        laskeLaajuudet(osa, viitteet);
      });

      // Osa
      if (rakenne._tutkinnonOsaViite) {
        rakenne.$laajuus = viitteet[rakenne._tutkinnonOsaViite].laajuus;
      }
      // Ryhmä
      else if (rakenne.osat) {
        if (rakenne.muodostumisSaanto) {
          var msl = rakenne.muodostumisSaanto.laajuus;
          if (msl) {
            rakenne.$vaadittuLaajuus = msl.maksimi || msl.minimi;
          }
        }
        rakenne.$laajuus = rakenne.rooli === 'määritelty' ? osienLaajuudenSumma(rakenne.osat) : rakenne.$vaadittuLaajuus;
      }
    }
    export const validoiRyhma = (rakenne, viitteet) => {
      var virheet = 0;

      function lajittele(osat) {
        var buckets = {};
        _.forEach(osat, function(osa) {
          if (!buckets[osa.$laajuus]) { buckets[osa.$laajuus] = 0; }
          buckets[osa.$laajuus] += 1;
        });
        return buckets;
      }

      function asetaVirhe(virhe, ms?) {
        rakenne.$virhe = {
          virhe: virhe,
          selite: kaannaSaanto(ms)
        };
        virheet += 1;
      }

      function avaintenSumma(osat, n, avaimetCb) {
        var res = 0;
        var i = n;
        var lajitellut = lajittele(osat);
        _.forEach(avaimetCb(lajitellut), function(k) {
          while (lajitellut[k]-- > 0 && i-- > 0) { res += parseInt(k, 10) || 0; }
        });
        return res;
      }

      if (!rakenne || !rakenne.osat) { return 0; }

      delete rakenne.$virhe;

      _.forEach(rakenne.osat, function(tosa) {
        if (!tosa._tutkinnonOsaViite) {
          virheet += validoiRyhma(tosa, viitteet) || 0;
        }
      });

      // On rakennemoduuli
      if (rakenne.muodostumisSaanto && rakenne.rooli !== 'määrittelemätön') {
        var ms = rakenne.muodostumisSaanto;
        var msl = ms.laajuus || 0;
        var msk = ms.koko || 0;

        if (msl && msk) {
          var minimi = avaintenSumma(rakenne.osat, msk.minimi, function(lajitellut) { return _.keys(lajitellut); });
          var maksimi = avaintenSumma(rakenne.osat, msk.maksimi, function(lajitellut) { return _.keys(lajitellut).reverse(); });
          if (minimi < msl.minimi) {
            asetaVirhe('rakenne-validointi-maara-laajuus-minimi', ms);
          }
          else if (maksimi < msl.maksimi) {
            asetaVirhe('rakenne-validointi-maara-laajuus-maksimi', ms);
          }
        } else if (msl) {
          // Validoidaan maksimi
          if (msl.maksimi) {
            if (osienLaajuudenSumma(rakenne.osat) < msl.maksimi) {
              asetaVirhe('muodostumis-rakenne-validointi-laajuus', ms);
            }
          }
        } else if (msk) {
          if (_.size(rakenne.osat) < msk.maksimi) {
            asetaVirhe('muodostumis-rakenne-validointi-maara', ms);
          }
        }
      }

      var tosat = _(rakenne.osat)
        .filter(function(osa) { return osa._tutkinnonOsaViite; })
        .value();

      if (_.size(tosat) !== _(tosat).uniq('_tutkinnonOsaViite').size()) {
        asetaVirhe('muodostumis-rakenne-validointi-uniikit');
      }

      return virheet;
    }
};
