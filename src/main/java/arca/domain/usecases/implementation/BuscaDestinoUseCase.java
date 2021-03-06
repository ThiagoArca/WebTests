package arca.domain.usecases.implementation;

import arca.controllers.network.RequestModel;
import arca.controllers.parse.ParseJson;
import arca.domain.entities.ConexaoOperadora;
import arca.domain.entities.Error;
import arca.domain.entities.Localidade;
import arca.domain.entities.ResultListaLocalidade;
import arca.domain.usecases.Params;
import arca.domain.usecases.Result;
import arca.domain.usecases.UseCase;
import arca.exceptions.NetworkException;
import arca.exceptions.ParseException;
import arca.logger.Logger;

public class BuscaDestinoUseCase extends UseCase<BuscaDestinoUseCase.BuscaDestinoResult, BuscaDestinoUseCase.BuscaDestinoParams> {

    private final String method = "buscaDestino?origem=%s";

    private final RequestModel requestModel;
    private final ParseJson<ResultListaLocalidade> parseJson;
    private final ConexaoOperadora conexaoOperadora;
    private final Logger logger;

    public BuscaDestinoUseCase(
            final RequestModel requestModel,
            final ParseJson<ResultListaLocalidade> parseJson,
            final ConexaoOperadora conexaoOperadora,
            final Logger logger
    ) {
        this.requestModel = requestModel;
        this.parseJson = parseJson;
        this.conexaoOperadora = conexaoOperadora;
        this.logger = logger;
    }

    @Override
    public BuscaDestinoResult execute(final BuscaDestinoParams params) {
        try {
            final String url = String.format(method, params.origem.id.toString());
            logger.add(String.format("%s%s", conexaoOperadora.url, url));
            return validate(requestModel.execute(conexaoOperadora, url, RequestModel.RequestType.GET));
        } catch (final NetworkException e) {
            return new BuscaDestinoResult(e);
        }
    }

    private BuscaDestinoResult validate(final RequestModel.ResponseModel model) {
        try {
            if (model.isSucess()) {
                return new BuscaDestinoResult(parseJson.parse(model.body));
            } else {
                return  new BuscaDestinoResult(model.error);
            }
        } catch (final ParseException pe) {
            return new BuscaDestinoResult(pe);
        }
    }

    public static class BuscaDestinoParams extends Params {
        public final Localidade origem;

        public BuscaDestinoParams(final Localidade origem) {
            this.origem = origem;
        }
    }

    public static class BuscaDestinoResult extends Result<ResultListaLocalidade> {
        public BuscaDestinoResult(ResultListaLocalidade result) {
            super(result);
        }

        public BuscaDestinoResult(Exception exception) {
            super(exception);
        }

        public BuscaDestinoResult(Error error) {
            super(error);
        }
    }
}
